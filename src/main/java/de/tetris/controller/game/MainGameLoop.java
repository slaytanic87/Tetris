package de.tetris.controller.game;

import com.fasterxml.jackson.core.type.TypeReference;
import de.tetris.controller.gui.ControllerContext;
import de.tetris.controller.gui.MainController;
import de.tetris.controller.interfaces.IGameController;
import de.tetris.model.*;
import de.tetris.model.block.*;
import de.tetris.model.data.Pos;
import de.tetris.service.EvaluationVerticle;
import de.tetris.utils.MessageEventUtils;
import io.vertx.core.json.Json;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Lam
 */
@Slf4j
public class MainGameLoop extends AnimationTimer implements IGameController {

    private TetrisField scope;

    private static final int MAX_QEUESIZE = 8;

    private double durationTime;
    private long lastTime;

    private boolean isButtonHit = false; // for smoother block animation
    private GameState gameState = GameState.getInstance();

    public MainGameLoop(TetrisField scope) {
        scope.setQueue(new LinkedBlockingQueue<>());
        this.scope = scope;
        scope.setCurrentBlock(generateBlock());
        generateBlocks(MAX_QEUESIZE);
        drawBlocksFromQueue();
        drawScore();
        durationTime = 0;
        lastTime = System.nanoTime();
    }

    /**
     * Gameloop
     * @param now nanoseconds
     */
    @Override
    public void handle(long now) {
        double diff = now - lastTime;

        if (isButtonHit) {
            refreshView();
            collisionHandling();
            isButtonHit = false;
            return;
        }
        if (diff >= durationTime) {
            refreshView();
            collisionHandling();
            scope.getCurrentBlock().moveDown(MainController.CELL_HEIGHT);
            lastTime = now;
            MessageEventUtils.getInstance().sendBlockToWebSocketBus(scope.getCurrentBlock());
        }
    }

    private void collisionHandling() {
        CollisionType collisionType = scope.detectCollisionInAdvance(scope.getCurrentBlock());
        switch (collisionType) {
            case BLOCK_LEFT:
            case BLOCK_RIGHT:
            case GROUND_BELOW:
            case BLOCK_BELOW:
                log.debug("collision type in advance is: {}", collisionType);
                scope.placeBlock(scope.getCurrentBlock());
                scope.updateBrickLevel(scope.getCurrentBlock());
                try {
                    scope.setCurrentBlock(scope.getQueue().take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                int processedRows = scope.processFilledRows();
                Scores.getInstance().addFinishRows(processedRows);
                Scores.getInstance().addScore(processedRows);
                if (processedRows > 0) {
                    scope.decreaseBrickLevelVec(processedRows);
                    ControllerContext.getMainController().drawFadeInScore(processedRows);
                }
                sendMetricForEvaluation();
                scope.getQueue().add(generateBlock());
                MessageEventUtils.getInstance().sendDataToWebSocketBus(scope.getFieldAsColorCells());
                log.debug("Current duration time: {} ns", durationTime);
                durationTime = scope.calcSpeed();
                log.debug("New duration time: {} ns", durationTime);
                drawBlocksFromQueue();
                break;
            case BLOCK_OVERFLOW:
                stopGame();
                break;
            case NONE:
                break;
            default: log.debug("Unknown collision type {}", collisionType);
        }
    }

    private void sendMetricForEvaluation() {
        MessageEventUtils.getInstance().sendDataToVertexBus(EvaluationVerticle.EVENT_BLOCK_TYPE,
                scope.getCurrentBlock().getBlockType());
        MessageEventUtils.getInstance().sendDataToVertexBus(EvaluationVerticle.EVENT_BLOCK_DISPLACE_VEC,
                scope.getCurrentBlock().getBlockDisplacementVec());
        MessageEventUtils.getInstance().sendDataToVertexBus(EvaluationVerticle.EVENT_FIELD_BRICK_LEVEL_VEC,
                scope.getBrickLevelVec());
        MessageEventUtils.getInstance().sendDataToVertexBus(EvaluationVerticle.EVENT_CALC_POSITION,
                scope.getCurrentBlock().getData().length, event -> {
            if (event.succeeded()) {
                String optimalPositionsJsonStr = event.result().body();
                if (optimalPositionsJsonStr != null && !optimalPositionsJsonStr.isEmpty()) {
                    Pos optimalPosition = Json.decodeValue(optimalPositionsJsonStr, Pos.class );
                    log.debug("Choosed position: {}", optimalPosition);
                    Block clonedBlock = scope.getCurrentBlock().clone();
                    clonedBlock.setColor(new Color(1.0,1.0, 1.0, 0.3));
                    clonedBlock.setGridposition(new GridPosition(optimalPosition.getPosX(),
                            optimalPosition.getPosY()));

                    CollisionType collisionType = scope.determineCollision(clonedBlock);
                    log.debug("Collision type for suggested block {}", collisionType);
                    if (collisionType.equals(CollisionType.BLOCK_BELOW) || collisionType.equals(CollisionType.BLOCK_RIGHT)
                    || collisionType.equals(CollisionType.BLOCK_LEFT)) {
                        log.debug("Suggested position {} collided", clonedBlock.getGridposition());
                        clonedBlock.getGridposition().setPosY(clonedBlock.getGridposition().getPosY() - 1);
                    }

                    clonedBlock.setTopLeft(new Point2D(clonedBlock.getGridposition().getPosX() * MainController.CELL_WIDTH,
                            clonedBlock.getGridposition().getPosY() * MainController.CELL_WIDTH));
                    gameState.setSuggestedBlockWithPosition(clonedBlock);
                }
            } else {
                log.debug("Could calculate optimal position cause: {}", event.cause().getMessage());
            }
        });
    }

    private void generateBlocks(int number) {
        for (int i = 0; i < number; i++) {
            Block block = generateBlock();
            log.debug("generate: {}", block.getClass().getSimpleName());
            scope.getQueue().add(block);
        }
    }

    private void refreshView() {
        ControllerContext.getMainController().clearGameFieldCanvas();
        drawTetrisScope();
        drawBlock(scope.getCurrentBlock());
        if (gameState.getSuggestedBlockWithPosition() != null) {
            drawBlock(gameState.getSuggestedBlockWithPosition());
        }
    }


    private void drawBlocksFromQueue() {
        final int VIEWCOLS = 2;
        final int VIEWROWS = 4;

        Block[] blocks = scope.getQueue().toArray(new Block[scope.getQueue().size()]);
        ControllerContext.getMainController().refreshBlockViewCanvas();
        ControllerContext.getMainController().drawBlockViewGrid(blocks.length / VIEWCOLS);
        drawScore();

        int colWidth = (int) Math.round(ControllerContext.getMainController()
                .getBlockViewDimension().getX() / VIEWCOLS);
        int rowHeight = (int) Math.round(ControllerContext.getMainController()
                .getBlockViewDimension().getY() / VIEWROWS);

        int cellWidth = Math.round(colWidth / VIEWROWS);
        int cellHeight = Math.round(rowHeight / VIEWROWS);

        Point2D pos = new Point2D(cellWidth, cellHeight);
        for (int i = 0; i < blocks.length; i++) {
            drawQueueBlock(pos, blocks[i]);
            if (i % 2 == 1) {
                pos = pos.subtract(pos.getX() - cellWidth, 0);
                pos = pos.add(0, rowHeight);
            } else {
                pos = pos.add(colWidth, 0);
            }
        }
    }

    private void drawQueueBlock(Point2D pos, Block block) {
        for (int row = 0; row < block.getData().length; row++) {
            double width = 0;
            for (int col = 0; col < block.getData()[row].length; col++) {
                if (block.getData()[row][col] == 1) {
                    ControllerContext.getMainController().drawBlockViewRect(pos, block.getColor());
                }
                pos = pos.add(MainController.CELL_WIDTH, 0);
                width += MainController.CELL_WIDTH;
            }
            pos = pos.add(0, MainController.CELL_HEIGHT);
            pos = pos.subtract(width, 0);
        }
    }

    private void drawTetrisScope() {
        List<List<Cell>> field = scope.getField();
        Point2D pos = new Point2D(0, 0);
        for (List<Cell> row: field) {
            double cols = 0;
            for (Cell cell: row) {
                if (cell.isFilled()) {
                    ControllerContext.getMainController().drawRectWithContour(pos, cell.getColor());
                }
                if (gameState.isDebug()) {
                    short value = cell.isFilled() ? (short) 1 : 0;
                    ControllerContext.getMainController().drawDebugRect(pos, Color.GRAY, value,
                            cell.getColor());
                }
                pos = pos.add(MainController.CELL_WIDTH, 0);
                cols += MainController.CELL_WIDTH;
            }
            pos = pos.add(0, MainController.CELL_HEIGHT);
            pos = pos.subtract(cols, 0);
        }
    }

    private void drawBlock(Block block) {
        int[][] data =  block.getData();
        Point2D point2D = block.getTopLeft();
        for (int i = 0; i < data.length; i++) {
            double colPtr = 0;
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == 1) {
                    ControllerContext.getMainController().drawRectWithContour(point2D, block.getColor());
                }
                point2D = point2D.add(MainController.CELL_WIDTH, 0);
                colPtr += MainController.CELL_WIDTH; // move to right
            }
            point2D = point2D.add(0, MainController.CELL_HEIGHT);
            point2D = point2D.subtract(colPtr, 0); // reset position to the left
        }
    }

    private void drawScore() {
        ControllerContext.getMainController().drawScore();
    }

    private Block generateBlock() {
        final double COLORCONST = 0.28;
        final int RANDOM_NUMBER_OF_BLOCKS = 6;

        Random random = new Random();
        int randNumber = random.nextInt(RANDOM_NUMBER_OF_BLOCKS) + 1;

        double r = Math.random();
        double g = Math.random();
        double b = Math.random();
        double o = 1.0;

        if (((r + g + b) / 3) < COLORCONST) {
            r = (r + COLORCONST) > 1.0 ? 1.0 : r + COLORCONST;
            g = (g + COLORCONST) > 1.0 ? 1.0 : g + COLORCONST;
            b = (b + COLORCONST) > 1.0 ? 1.0 : b + COLORCONST;
        }
        Color color = new Color(r, g, b, o);

        switch (randNumber) {
            case 1: return new Iblock(color);
            case 2: return new Jblock(color);
            case 3: return new Lblock(color);
            case 4: return new Oblock(color);
            case 5: return new Sblock(color);
            case 6: return new Tblock(color);
            default: return new Zblock(color);
        }
    }

    private void initGame() {
        scope.resetModel();
        scope.getQueue().clear();
        Scores.getInstance().resetScores();
        scope.setCurrentBlock(generateBlock());
        generateBlocks(MAX_QEUESIZE);
        sendMetricForEvaluation();
        drawBlocksFromQueue();
        drawScore();
        refreshView();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void start() {
        super.start();
    }

    public void setDuration(Duration duration) {
        long initNanos = TimeUnit.MILLISECONDS.toNanos((long) duration.toMillis());
        TetrisField.INITIAL_LOWEST_SPEED_IN_MILLI = initNanos;
        durationTime = initNanos;
    }

    /****************************** Controller event section *********************************/

    @Override
    public void moveLeft() {
        isButtonHit = true;
        if (scope.getCurrentBlock().isMoveLeftAllowed(scope.getField())) {
            scope.getCurrentBlock().moveLeft(MainController.CELL_WIDTH);
        }
    }

    @Override
    public void moveRight() {
        isButtonHit = true;
        if (scope.getCurrentBlock().isMoveRightAllowed(scope.getField())) {
            scope.getCurrentBlock().moveRight(MainController.CELL_WIDTH);
        }
    }

    @Override
    public void moveDown() {
        isButtonHit = true;
        collisionHandling();
        scope.getCurrentBlock().moveDown(MainController.CELL_HEIGHT);
    }

    @Override
    public void moveUp() {
        // TODO neccessary?
    }

    @Override
    public void rotateLeft() {
        isButtonHit = true;
        if (scope.getCurrentBlock().isRotateLeftAllowed(scope.getField())) {
            scope.getCurrentBlock().rotateLeft();
            sendMetricForEvaluation();
        }
    }

    @Override
    public void rotateRight() {
        isButtonHit = true;
        if (scope.getCurrentBlock().isRotateRightAllowed(scope.getField())) {
            scope.getCurrentBlock().rotateRight();
            sendMetricForEvaluation();
        }
    }

    @Override
    public void toggleModelView() {
        if (gameState.isDebug()) {
            gameState.debugOff();
        } else {
            gameState.debugOn();
        }
        log.debug("Debug mode: {}", gameState.isDebug());
    }

    @Override
    public void startGame() {
        if (gameState.isFinished()) {
            gameState.setIsNotStopped();
            gameState.setIsNotFinished();
            initGame();
            ControllerContext.getMainController().startButtonAnimationOff();
            ControllerContext.getMainController().pauseAnimationOff();
            // start main gameloop
            super.start();
        }
        log.debug("Game started!");
    }

    @Override
    public void stopGame() {
        if (gameState.isStopped()) {
            return;
        }
        gameState.setIsStopped();
        gameState.setIsFinished();
        ControllerContext.getMainController().clearGameFieldCanvas();
        ControllerContext.getMainController().startButtonAnimationOn();
        ControllerContext.getMainController().pauseAnimationOff();
        this.stop();
        Scores.getInstance().resetScores();
        scope.resetModel();
        log.debug("Game stopped!");
    }

    @Override
    public void pause() {
        if (gameState.isPaused()) {
            gameState.setIsNotPaused();
            ControllerContext.getMainController().pauseAnimationOff();
            super.start();
        } else {
            gameState.setIsPaused();
            super.stop();
            ControllerContext.getMainController().pauseAnimationOn();
        }
    }

}
