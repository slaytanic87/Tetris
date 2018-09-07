package de.tetris.controller.gui;

import de.tetris.controller.gui.animation.CathedralAnimation;
import de.tetris.controller.gui.animation.UiMenuAnimation;
import de.tetris.model.Scores;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Lam
 */
@Slf4j
public class MainController implements Initializable {

    public static double CELL_WIDTH = 20;
    public static double CELL_HEIGHT = 20;

    @FXML
    private Canvas gameView;

    @FXML
    private Canvas blockView;

    @FXML
    private Canvas scoreView;

    @FXML
    private Canvas canvasCathedral;

    @FXML
    private AnchorPane canvasPane;

    private GraphicsContext graphicsContext;
    private GraphicsContext blockQueueViewContext;
    private GraphicsContext scoreViewContext;

    private UiMenuAnimation animation;
    private CathedralAnimation cathedralAnimation;

    private Font globalFont;
    public static final String FONT_PATH = "/fxml/fonts/PinballChallenge.ttf";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        graphicsContext = gameView.getGraphicsContext2D();
        blockQueueViewContext = blockView.getGraphicsContext2D();
        scoreViewContext = scoreView.getGraphicsContext2D();

        globalFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 20);
        scoreViewContext.setFont(globalFont);
        
        animation = new UiMenuAnimation();
        animation.createMenuStart(graphicsContext, gameView.getWidth(), gameView.getHeight());
        animation.createPauseAnimation(graphicsContext, gameView.getWidth(), gameView.getHeight());
        cathedralAnimation = new CathedralAnimation(canvasCathedral);
        cathedralAnimation.start();
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
        graphicsContext.fillRect(topLeftpos.getX(), topLeftpos.getY(), CELL_WIDTH - 2, CELL_HEIGHT - 2);
    }

    public void drawDebugRect(Point2D topLeftpos, Color color, int model, Color fontColor) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(topLeftpos.getX(), topLeftpos.getY(), CELL_WIDTH - 2, CELL_HEIGHT - 2);

        Color fColor = (fontColor == null) ? Color.AQUAMARINE : fontColor;
        graphicsContext.setFill(fColor);
        graphicsContext.fillText(String.valueOf(model), topLeftpos.getX() + (CELL_WIDTH / 3),
                topLeftpos.getY() + (CELL_HEIGHT / 2) + 2);
    }

    public void clearGameFieldCanvas() {
        graphicsContext.clearRect(0, 0, gameView.getWidth(), gameView.getHeight());
    }

    public void refreshBlockViewCanvas() {
        blockQueueViewContext.clearRect(0, 0, blockView.getWidth(), blockView.getHeight());
    }

    public void drawBlockViewRect(Point2D topLeftpos, Color color) {
        blockQueueViewContext.setFill(color);
        blockQueueViewContext.fillRect(topLeftpos.getX(), topLeftpos.getY(), CELL_WIDTH - 2, CELL_HEIGHT - 2);
    }

    public void drawScore() {
        scoreViewContext.clearRect(0, 0, scoreView.getWidth(), scoreView.getHeight());
        scoreViewContext.setFill(Color.BLUEVIOLET);
        Text scoreText = new Text("Score " + String.valueOf(Scores.getInstance().getMainscore()));
        scoreText.setFont(globalFont);
        Text levelText = new Text("Level " + String.valueOf(Scores.getInstance().getLevel()));
        levelText.setFont(globalFont);

        double posY = (scoreView.getHeight() / 2) - scoreText.getBoundsInLocal().getHeight();
        double blockDistance = (scoreView.getHeight() / 2) + levelText.getBoundsInLocal().getHeight();

        scoreViewContext.fillText(scoreText.getText(), 10, posY);
        scoreViewContext.setFill(Color.AQUAMARINE);
        scoreViewContext.fillText(levelText.getText(), 10, blockDistance);
    }

    public void drawFadeInScore(int processedRows) {
        final int animationDuration = 1000;
        Text newScoreText = new Text("+" + String.valueOf(processedRows * Scores.getInstance().ROWPOINT));
        newScoreText.setFill(Color.YELLOW);
        newScoreText.setFont(new Font(35));

        double xpos = gameView.getWidth() / 2;
        double ypos = gameView.getHeight() / 2;
        newScoreText.setX(xpos);
        newScoreText.setY(ypos);

        canvasPane.getChildren().add(newScoreText);
        FadeTransition ft = new FadeTransition(Duration.millis(animationDuration), newScoreText);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        Path path = new Path();
        // start point
        path.getElements().add(new MoveTo(xpos, ypos));
        // move to
        path.getElements().add(new LineTo(xpos, ypos - 100));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(animationDuration));
        pathTransition.setPath(path);
        pathTransition.setNode(newScoreText);
        pathTransition.setOrientation(PathTransition.OrientationType.NONE);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(true);

        ParallelTransition seTransition = new ParallelTransition();
        seTransition.getChildren().addAll(pathTransition, ft);
        seTransition.setCycleCount(1);
        seTransition.play();
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

    public void calcCellSize(int cols, int rows) {
        CELL_WIDTH = gameView.getWidth() / cols;
        CELL_HEIGHT = gameView.getHeight() / rows;
    }

}
