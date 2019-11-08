package de.tetris.model.block;

import de.tetris.controller.gui.MainController;
import de.tetris.model.Cell;
import de.tetris.model.GridPosition;
import de.tetris.model.TetrisField;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import lombok.Data;

import java.util.List;

/**
 * ##
 * ##
 *
 * Created by lehon on 31.10.2016.
 */
@Data
public class Oblock extends Block {

    private int[][] data = {{1,1},
                            {1,1}};

    public Oblock(Color color) {
        super(color);
        int colmiddle = (TetrisField.COLS / 2) - (data[0].length / 2);
        super.position = new GridPosition(colmiddle, 0);
        super.topLeft = new Point2D(colmiddle * MainController.CELL_WIDTH, 0);
        super.blockHeight = data.length;
        super.blockWidth = data[0].length;
        super.calcDisplacement(data);
    }

    public void rotateLeft() {
        // rotate doesn't matter
    }

    public void rotateRight() {
        // rotate doesn't matter
    }

    @Override
    public boolean isRotateLeftAllowed(List<List<Cell>> field) {
        // rotate doesn't matter
        return true;
    }

    @Override
    public boolean isRotateRightAllowed(List<List<Cell>> field) {
        // rotate doesn't matter
        return true;
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
    public BlockType getBlockType() {
        return BlockType.O;
    }

    @Override
    public Block clone() {
        Color tmp = getColor();
        Oblock oblock = new Oblock(new Color(tmp.getRed(), tmp.getGreen(), tmp.getBlue(), 1.0));
        oblock.setData(deepGridDataCopy(getData()));
        oblock.setGridposition(super.getGridposition().clone());
        Point2D topLeftTmp = super.getTopLeft();
        oblock.setTopLeft(new Point2D(topLeftTmp.getX(), topLeftTmp.getY()));
        oblock.setBlockHeight(getBlockHeight());
        oblock.setBlockWidth(getBlockWidth());
        oblock.setBlockDisplacementVec(deepArrayCopy(super.getBlockDisplacementVec()));
        return oblock;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\n");
        for (int[] datum : data) {
            for (int j = 0; j < data[0].length; j++) {
                str.append(datum[j]).append("|");
            }
            str.append("\n");
        }
        return str.toString();
    }
}
