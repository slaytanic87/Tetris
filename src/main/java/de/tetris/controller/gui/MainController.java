package de.tetris.controller.gui;

import de.tetris.controller.game.Scores;
import de.tetris.controller.gui.animation.UiAnimation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Lam
 */
@Slf4j
public class MainController implements Initializable {

    public static double BLOCK_WIDTH = 20;
    public static double BLOCK_HEIGHT = 20;

    @FXML
    private Canvas gameView;

    @FXML
    private Canvas blockView;

    @FXML
    private Canvas scoreView;

    @FXML
    private AnchorPane canvasPane;

    private GraphicsContext graphicsContext;
    private GraphicsContext blockQueueViewContext;
    private GraphicsContext scoreViewContext;

    private UiAnimation animation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        graphicsContext = gameView.getGraphicsContext2D();
        blockQueueViewContext = blockView.getGraphicsContext2D();
        scoreViewContext = scoreView.getGraphicsContext2D();
        animation = new UiAnimation();
        animation.createMenuStart(graphicsContext, gameView.getWidth(), gameView.getHeight());
        animation.createPauseAnimation(graphicsContext, gameView.getWidth(), gameView.getHeight());
        startButtonAnimationOn();
    }

    public void pauseAnimationOn() {
        animation.pauseAnimationOn();
    }

    public void pauseAnimationOff() {
        animation.pauseAnimationOff();
    }

    public void startButtonAnimationOn() {
        animation.startButtonAnimationOn();
    }

    public void startButtonAnimationOff() {
        animation.startButtonAnimationOff();
    }

    public void drawRectWithContour(Point2D topLeftpos, Color color) {
        // draw rect
        Stop[] stops = new Stop[] { new Stop(0, color), new Stop(1.8, Color.BLACK)};
        RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.9, true, CycleMethod.NO_CYCLE, stops);
        graphicsContext.setFill(rg);
        // shrink rect to visualize contour
        graphicsContext.fillRect(topLeftpos.getX(), topLeftpos.getY(), BLOCK_WIDTH - 2, BLOCK_HEIGHT - 2);
    }

    public void drawDebugRect(Point2D topLeftpos, Color color, int model, Color fontColor) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(topLeftpos.getX(), topLeftpos.getY(), BLOCK_WIDTH - 2, BLOCK_HEIGHT - 2);

        Color fColor = (fontColor == null) ? Color.AQUAMARINE : fontColor;
        graphicsContext.setFill(fColor);
        graphicsContext.fillText(String.valueOf(model), topLeftpos.getX() + (BLOCK_WIDTH / 3),
                topLeftpos.getY() + (BLOCK_HEIGHT / 2) + 2);
    }

    public void clearGameFieldCanvas() {
        graphicsContext.clearRect(0, 0, gameView.getWidth(), gameView.getHeight());
    }

    public void refreshBlockViewCanvas() {
        blockQueueViewContext.clearRect(0, 0, blockView.getWidth(), blockView.getHeight());
    }

    public void drawBlockViewRect(Point2D topLeftpos, Color color) {
        blockQueueViewContext.setFill(color);
        blockQueueViewContext.fillRect(topLeftpos.getX(), topLeftpos.getY(), BLOCK_WIDTH - 2, BLOCK_HEIGHT - 2);
    }

    public void drawScore() {
        scoreViewContext.clearRect(0, 0, scoreView.getWidth(), scoreView.getHeight());
        scoreViewContext.setFill(Color.BLUEVIOLET);
        double posY = scoreView.getHeight() / 2 + 2;
        double blockDistance = posY / 2;
        scoreViewContext.fillText("Score " + String.valueOf(Scores.INSTANCE.getMainscore()), 10, blockDistance);
        scoreViewContext.setFill(Color.AQUAMARINE);
        scoreViewContext.fillText("Level " + String.valueOf(Scores.INSTANCE.getLevel()), 10, posY + blockDistance);
    }

    public void drawBlockViewGrid(int lines) {
        final int cellwidth = (int) Math.round(blockView.getWidth() / 2);
        final int cellheight = (int) Math.round(blockView.getHeight() / lines);
        blockQueueViewContext.setStroke(Color.GRAY);
        blockQueueViewContext.setLineWidth(0.5);
        // draw horizontal line
        int rowpos = cellheight;
        for (int i = 0; i < lines - 1; i++) {
            blockQueueViewContext.strokeLine(0, rowpos, blockView.getWidth(), rowpos);
            rowpos += cellheight;
        }
        // draw vertical line
        blockQueueViewContext.strokeLine(cellwidth, 0, cellwidth, blockView.getHeight());
    }

    public Point2D getGameFieldDimension() {
        return new Point2D(gameView.getWidth(), gameView.getHeight());
    }

    public Point2D getBlockViewDimension() {
        return new Point2D(blockView.getWidth(), blockView.getHeight());
    }

    public void calcBlockSize(int cols, int rows) {
        BLOCK_WIDTH = gameView.getWidth() / cols;
        BLOCK_HEIGHT = gameView.getHeight() / rows;
    }

}