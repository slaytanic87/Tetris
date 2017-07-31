package de.tetris.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Lam
 */
@Builder
@Data
public class GridPosition {
    private int posX;
    private int posY;

    public GridPosition(int x, int y) {
        posX = x;
        posY = y;
    }

    public GridPosition substract(int dx, int dy) {
        posX -= dx;
        posY -= dy;
        return this;
    }

    public GridPosition addition(int dx, int dy) {
        posX += dx;
        posY += dy;
        return this;
    }
}
