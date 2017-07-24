package de.tetris.model.block;

import de.tetris.controller.gui.MainController;
import de.tetris.model.Cell;
import de.tetris.model.GridPosition;
import de.tetris.model.TetrisField;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * ######
 *
 * @author Lam
 */
@Data
@Slf4j
public class Iblock extends Block {

    public int[][] data = { {1,1,1,1} };

    public Iblock(Color color) {
        super(color);
        int colmiddle = (TetrisField.COLS / 2) - (data[0].length / 2);
        super.position = new GridPosition(colmiddle, 0);
        super.topLeft = new Point2D(colmiddle * MainController.BLOCK_WIDTH, 0);
        super.blockHeight = data.length;
        super.blockWidth = data[0].length;
    }

    @Override
    public void rotateLeft() {
        data = super.tiltLeft(data);
    }

    @Override
    public void rotateRight() {
        data = super.tiltRight(data);
    }

    @Override
    public boolean isRotateLeftAllowed(List<List<Cell>> field) {
        return super.isNotOverlap(super.tiltLeft(data), field, getGridposition());
    }

    @Override
    public boolean isRotateRightAllowed(List<List<Cell>> field) {
        return super.isNotOverlap(super.tiltRight(data), field, getGridposition());
    }

    @Override
    public boolean isMoveLeftAllowed(List<List<Cell>> field) {
        if (super.isNotBehindLeft()) {
            GridPosition gridPosition = new GridPosition(getGridposition().getPosX() - 1,
                    getGridposition().getPosY());
            return super.isNotOverlap(data, field, gridPosition);
        }
        return false;
    }

    @Override
    public boolean isMoveRightAllowed(List<List<Cell>> field) {
        if (super.isNotAfterRight()) {
            GridPosition gridPosition = new GridPosition(getGridposition().getPosX() + 1,
                    getGridposition().getPosY());
            return super.isNotOverlap(data, field, gridPosition);
        }
        return false;
    }

    @Override
    public int getBlockWidth() {
        return super.blockWidth;
    }

    @Override
    public int getBlockHeight() {
        return super.blockHeight;
    }


    @Override
    public String toString() {
        String str = "\n";
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
             str += data[i][j] + "|";
            }
            str += "\n";
        }
        return str;
    }

}
