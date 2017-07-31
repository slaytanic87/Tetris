package de.tetris.controller.gui.animation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Lam
 */
@Data
public class CathedralAnimation extends AnimationTimer {

    private static final int NUMBER_OF_TILES = 18;
    private static final double SECOND_AS_NANOSECONDS = 1000000000.0;
    private static final double DURATIONPERFRAME = 0.10;
    private static final String BASE_PATH = "/fxml/images/cathedral/cathedral";

    private long lastTime;
    private double durationTime;

    private List<Image> tilesets = new LinkedList<>();

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    public CathedralAnimation(Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();

        for (int i = 1; i <= NUMBER_OF_TILES; i++) {
            tilesets.add(new Image(this.getClass().getResourceAsStream(BASE_PATH + i + ".png")));
        }
        lastTime = System.nanoTime();
    }

    /**
     * Animation loop.
     * @param now in Nanoseconds
     */
    @Override
    public void handle(long now) {
        double diff = now - lastTime;
        double timeDiffAsSeconds = (diff / SECOND_AS_NANOSECONDS);
        int index = (int)((timeDiffAsSeconds % (NUMBER_OF_TILES * DURATIONPERFRAME)) / DURATIONPERFRAME);
        graphicsContext.drawImage(tilesets.get(index), 0, 0, canvas.getWidth(), canvas.getHeight());
    }
}