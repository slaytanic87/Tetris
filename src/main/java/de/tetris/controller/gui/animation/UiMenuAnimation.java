package de.tetris.controller.gui.animation;

import de.tetris.controller.gui.MainController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * @author Lam
 */
public class UiMenuAnimation {

    private Timeline pauseAnimation;
    private Timeline menuStartAnimation;

    public void createMenuStart(GraphicsContext graphicsContext, double scopeWidth, double scopeHeight) {
        menuStartAnimation = new Timeline();
        menuStartAnimation.setCycleCount(Timeline.INDEFINITE);
        final String controlsStr = "CTRL ALT ← ↓ →";
        final String inStr = "A: About";
        // TODO Richtige mathematische Breiten-/Höhenrechnung
        final double instrX = (scopeWidth / 2) - (3 * MainController.BLOCK_WIDTH);
        final double instrY = (scopeHeight / 2) + (2 * MainController.BLOCK_HEIGHT);

        menuStartAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(400), actionEvent -> {
                    graphicsContext.setFill(Color.BLUE);
                    graphicsContext.setFont(new Font(14));
                    graphicsContext.fillText("- Press Space -", (scopeWidth / 2) - 2 * MainController.BLOCK_WIDTH,
                            scopeHeight / 2);
                    graphicsContext.setFont(new Font(18));
                    graphicsContext.fillText(controlsStr, instrX, instrY);
                    // TODO Richtige mathematische Breiten-/Höhenrechnung
                    graphicsContext.fillText(inStr, (scopeWidth / 2) - MainController.BLOCK_WIDTH
                                    - (MainController.BLOCK_WIDTH / 2),
                            instrY + (2 * MainController.BLOCK_HEIGHT));
                }, new KeyValue[0]) // don't use binding
        );
        menuStartAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(800), actionEvent -> {
                    graphicsContext.clearRect(0, 0, scopeWidth, scopeHeight);
                    graphicsContext.setFont(new Font(18));
                    graphicsContext.fillText(controlsStr, instrX, instrY);
                    // TODO Richtige mathematische Breiten-/Höhenrechnung
                    graphicsContext.fillText(inStr, (scopeWidth / 2) - MainController.BLOCK_WIDTH
                                    - (MainController.BLOCK_WIDTH / 2),
                            instrY + (2 * MainController.BLOCK_HEIGHT));
                }, new KeyValue[0]) // don't use binding);
        );
    }

    public void createPauseAnimation(GraphicsContext graphicsContext, double scopeWidth, double scopeHeight) {
        pauseAnimation = new Timeline();
        final String pauseStr = "- Paused Game -";
        pauseAnimation.setCycleCount(Timeline.INDEFINITE);
        pauseAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(200), actionEvent -> {
                    graphicsContext.clearRect(0, 0, scopeWidth, scopeHeight);
                    graphicsContext.setFill(Color.YELLOWGREEN);
                    graphicsContext.setFont(Font.getDefault());
                    graphicsContext.fillText(pauseStr,
                            (scopeWidth / 2) - 2.5 * MainController.BLOCK_WIDTH, scopeHeight / 2);
                }, new KeyValue[0]) // don't use binding
        );
        pauseAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(400), actionEvent -> {
                    graphicsContext.setFill(Color.GREEN);
                    graphicsContext.fillText(pauseStr,
                            (scopeWidth / 2) - 2.5 * MainController.BLOCK_WIDTH, scopeHeight / 2);
                }, new KeyValue[0]) // don't use binding
        );
    }

    public void pauseAnimationOn() {
        pauseAnimation.play();
    }

    public void pauseAnimationOff() {
        pauseAnimation.stop();
    }

    public void startButtonAnimationOn() {
        menuStartAnimation.play();
    }

    public void startButtonAnimationOff() {
        menuStartAnimation.stop();
    }

}
