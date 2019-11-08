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
 *    #
 * ####
 *
 * @author Lam
 */
@Data
public class Lblock extends Block {

    private int[][] data = { {0,0,1},
                             {1,1,1} };

    public Lblock(Color color) {
        super(color);
        int colmiddle = (TetrisField.COLS / 2) - (data[0].length / 2);
        super.position = new GridPosition(colmiddle, 0);
        super.topLeft = new Point2D(colmiddle * MainController.CELL_WIDTH, 0);
        super.blockHeight = data.length;
        super.blockWidth = data[0].length;
        super.calcDisplacement(data);
    }

    public void rotateLeft() {
        data = super.tiltLeft(data);
    }

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
    public BlockType getBlockType() {
        return BlockType.L;
    }

    @Override
    public Block clone() {
        Color tmp = getColor();
        Lblock lblock = new Lblock(new Color(tmp.getRed(), tmp.getGreen(), tmp.getBlue(), 1.0));
        lblock.setData(deepGridDataCopy(getData()));
        lblock.setGridposition(super.getGridposition().clone());
        Point2D topLeftTmp = super.getTopLeft();
        lblock.setTopLeft(new Point2D(topLeftTmp.getX(), topLeftTmp.getY()));
        lblock.setBlockHeight(getBlockHeight());
        lblock.setBlockWidth(getBlockWidth());
        lblock.setBlockDisplacementVec(deepArrayCopy(super.getBlockDisplacementVec()));
        return lblock;
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
