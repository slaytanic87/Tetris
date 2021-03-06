package de.tetris.controller.gui.animation;

import de.tetris.controller.gui.MainController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static de.tetris.controller.gui.MainController.FONT_PATH;

/**
 * @author Lam
 */
public class UiMenuAnimation {

    private Timeline pauseAnimation;
    private Timeline menuStartAnimation;

    public void createMenuStart(GraphicsContext graphicsContext, double scopeWidth, double scopeHeight) {
        menuStartAnimation = new Timeline();
        menuStartAnimation.setCycleCount(Timeline.INDEFINITE);

        final Text pressSpace = new Text("- Press Space -");
        final Text control = new Text("Ctrl Alt");
        final Text arrow = new Text("Left Down Right");
        final Text about = new Text("A: About");

        final Font spaceFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 25);
        final Font keymapFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 20);

        pressSpace.setFont(spaceFont);
        control.setFont(keymapFont);
        about.setFont(keymapFont);
        arrow.setFont(keymapFont);

        final double spaceStrX = (scopeWidth - pressSpace.getBoundsInLocal().getWidth()) / 2;
        // TODO Richtige mathematische Höhenrechnung ?
        final double spaceStrY = scopeHeight / 2;

        final double controlStrX = (scopeWidth - control.getBoundsInLocal().getWidth()) / 2;
        // TODO Richtige mathematische Höhenrechnung ?
        final double controlStrY = spaceStrY + (2 * MainController.CELL_HEIGHT);

        final double keyarrowStrX = (scopeWidth - arrow.getBoundsInLocal().getWidth()) / 2;
        // TODO Richtige mathematische Höhenrechnung ?
        final double keyarrowStrY = controlStrY + (2 * MainController.CELL_HEIGHT);

        final double aboutStrX = (scopeWidth - about.getBoundsInLocal().getWidth()) / 2;
        // TODO Richtige mathematische Höhenrechnung ?
        final double aboutStrY = keyarrowStrY + (2 * MainController.CELL_HEIGHT);

        menuStartAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(400), actionEvent -> {
                    graphicsContext.setFill(Color.GREEN);
                    graphicsContext.setFont(spaceFont);
                    graphicsContext.fillText(pressSpace.getText(), spaceStrX, spaceStrY);
                    graphicsContext.setFill(Color.BLUE);

                    graphicsContext.setFont(keymapFont);
                    graphicsContext.fillText(control.getText(), controlStrX, controlStrY);
                    graphicsContext.fillText(arrow.getText(), keyarrowStrX, keyarrowStrY);
                    graphicsContext.fillText(about.getText(), aboutStrX, aboutStrY);
                }, new KeyValue[0]) // don't use binding
        );
        menuStartAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(800), actionEvent -> {
                    graphicsContext.clearRect(0, 0, scopeWidth, scopeHeight);
                    graphicsContext.setFont(keymapFont);
                    graphicsContext.fillText(control.getText(), controlStrX, controlStrY);
                    graphicsContext.fillText(arrow.getText(), keyarrowStrX, keyarrowStrY);
                    graphicsContext.fillText(about.getText(), aboutStrX, aboutStrY);
                }, new KeyValue[0]) // don't use binding);
        );
    }

    public void createPauseAnimation(GraphicsContext graphicsContext, double scopeWidth, double scopeHeight) {
        pauseAnimation = new Timeline();
        final Text pause = new Text("- Paused Game -");
        final Font pauseFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 20);
        pause.setFont(pauseFont);

        final double xpos = (scopeWidth - pause.getBoundsInLocal().getWidth()) / 2;
        final double ypos = scopeHeight / 2;

        pauseAnimation.setCycleCount(Timeline.INDEFINITE);
        pauseAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(200), actionEvent -> {
                    graphicsContext.clearRect(0, 0, scopeWidth, scopeHeight);
                    graphicsContext.setFill(Color.YELLOWGREEN);
                    graphicsContext.setFont(pauseFont);
                    graphicsContext.fillText(pause.getText(), xpos, ypos);
                }, new KeyValue[0]) // don't use binding
        );
        pauseAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(400), actionEvent -> {
                    graphicsContext.setFill(Color.GREEN);
                    graphicsContext.fillText(pause.getText(), xpos, ypos);
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