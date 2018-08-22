package de.tetris.controller.game;

import de.tetris.controller.gui.GlobalController;
import de.tetris.controller.gui.MainController;
import de.tetris.controller.interfaces.IGameController;
import de.tetris.model.*;
import de.tetris.model.block.*;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

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

    private LinkedBlockingQueue<Block> queue;

    private Block currentBlock;

    private double durationTime;
    private long lastTime;

    private boolean isButtonHit = false; // for smoother block animation
    private GameState gameState = GameState.getInstance();

    public MainGameLoop(TetrisField scope) {
        queue = new LinkedBlockingQueue<>();
        this.scope = scope;
        currentBlock = generateBlock();
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
            currentBlock.moveDown(MainController.CELL_HEIGHT);
            lastTime = now;
        }
    }

    private void collisionHandling() {
        CollisionType collisionType = scope.detectFutureCollision(currentBlock);
        switch (collisionType) {
            case GROUND_BELOW:
            case BLOCK_BELOW:
                scope.placeBlock(currentBlock);
                try {
                    currentBlock = queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                queue.add(generateBlock());
                int processedRows = scope.processFilledRows();
                Scores.INSTANCE.addFinishRows(processedRows);
                Scores.INSTANCE.addScore(processedRows);
                if (processedRows > 0) {
                    GlobalController.getMainController().drawFadeInScore(processedRows);
                }
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
            default: log.debug("Nothing to do with this collisiontype {}", collisionType);
        }
    }

    public void generateBlocks(int number) {
        for (int i = 0; i < number; i++) {
            Block block = generateBlock();
            log.debug("generate: {}", block.getClass().getSimpleName());
            queue.add(block);
        }
    }

    private void refreshView() {
        GlobalController.getMainController().clearGameFieldCanvas();
        drawTetrisScope();
        drawBlock(currentBlock);
    }


    protected void drawBlocksFromQueue() {
        final int VIEWCOLS = 2;
        final int VIEWROWS = 4;

        Block[] blocks = queue.toArray(new Block[queue.size()]);
        GlobalController.getMainController().refreshBlockViewCanvas();
        GlobalController.getMainController().drawBlockViewGrid(blocks.length / VIEWCOLS);
        drawScore();

        int colWidth = (int) Math.round(GlobalController.getMainController()
                .getBlockViewDimension().getX() / VIEWCOLS);
        int rowHeight = (int) Math.round(GlobalController.getMainController()
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
                    GlobalController.getMainController().drawBlockViewRect(pos, block.getColor());
                }
                pos = pos.add(MainController.CELL_WIDTH, 0);
                width += MainController.CELL_WIDTH;
            }
            pos = pos.add(0, MainController.CELL_HEIGHT);
            pos = pos.subtract(width, 0);
        }
    }

    protected void drawTetrisScope() {
        List<List<Cell>> field = scope.getField();
        Point2D pos = new Point2D(0, 0);
        for (List<Cell> row: field) {
            double cols = 0;
            for (Cell cell: row) {
                if (cell.isFilled()) {
                    GlobalController.getMainController().drawRectWithContour(pos, cell.getColor());
                }
                if (gameState.isDebug()) {
                    short value = cell.isFilled() ? (short) 1 : 0;
                    GlobalController.getMainController().drawDebugRect(pos, Color.GRAY, value,
                            cell.getColor());
                }
                pos = pos.add(MainController.CELL_WIDTH, 0);
                cols += MainController.CELL_WIDTH;
            }
            pos = pos.add(0, MainController.CELL_HEIGHT);
            pos = pos.subtract(cols, 0);
        }
    }

    protected void drawBlock(Block block) {
        int[][] data =  block.getData();
        Point2D point2D = block.getTopLeft();
        for (int i = 0; i < data.length; i++) {
            double colPtr = 0;
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == 1) {
                    GlobalController.getMainController().drawRectWithContour(point2D, block.getColor());
                }
                point2D = point2D.add(MainController.CELL_WIDTH, 0);
                colPtr += MainController.CELL_WIDTH; // move to right
            }
            point2D = point2D.add(0, MainController.CELL_HEIGHT);
            point2D = point2D.subtract(colPtr, 0); // reset position to the left
        }
    }

    protected void drawScore() {
        GlobalController.getMainController().drawScore();
    }

    public Block generateBlock() {
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
        queue.clear();
        Scores.INSTANCE.resetScores();
        currentBlock = generateBlock();
        generateBlocks(MAX_QEUESIZE);
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
        if (currentBlock.isMoveLeftAllowed(scope.getField())) {
            currentBlock.moveLeft(MainController.CELL_WIDTH);
        }
    }

    @Override
    public void moveRight() {
        isButtonHit = true;
        if (currentBlock.isMoveRightAllowed(scope.getField())) {
            currentBlock.moveRight(MainController.CELL_WIDTH);
        }
    }

    @Override
    public void moveDown() {
        isButtonHit = true;
        collisionHandling();
        currentBlock.moveDown(MainController.CELL_HEIGHT);
    }

    @Override
    public void moveUp() {
        // TODO neccessary?
    }

    @Override
    public void rotateLeft() {
        isButtonHit = true;
        if (currentBlock.isRotateLeftAllowed(scope.getField())) {
            currentBlock.rotateLeft();
        }
    }

    @Override
    public void rotateRight() {
        isButtonHit = true;
        if (currentBlock.isRotateRightAllowed(scope.getField())) {
            currentBlock.rotateRight();
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
            GlobalController.getMainController().startButtonAnimationOff();
            GlobalController.getMainController().pauseAnimationOff();
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
        GlobalController.getMainController().clearGameFieldCanvas();
        GlobalController.getMainController().startButtonAnimationOn();
        GlobalController.getMainController().pauseAnimationOff();
        this.stop();
        log.debug("Game stopped!");
    }

    @Override
    public void pause() {
        if (gameState.isPaused()) {
            gameState.setIsNotPaused();
            GlobalController.getMainController().pauseAnimationOff();
            super.start();
        } else {
            gameState.setIsPaused();
            super.stop();
            GlobalController.getMainController().pauseAnimationOn();
        }
    }

}
