package de.tetris.model;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Lam
 */
@Data
@Builder
@AllArgsConstructor
public class Cell {

    private boolean filled;

    private Color color;

    public Cell() {
        filled = false;
    }

}
