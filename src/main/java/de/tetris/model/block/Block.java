package de.tetris.model.block;

import de.tetris.controller.gui.MainController;
import de.tetris.model.Cell;
import de.tetris.model.GridPosition;
import de.tetris.model.TetrisField;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lam
 */
@Slf4j
public abstract class Block implements Cloneable {

    protected Color color;

    /**
     * Top left graphic coordinate of the block tile
     */
    protected Point2D topLeft;

    /**
     * Top left array index position in the game grid
     */
    protected GridPosition position;

    protected int blockWidth;
    protected int blockHeight;

    private int[] displacementVec;

    public Block(Color color) {
        this.color = color;
    }

    protected int[][] tiltRight(int[][] data) {
        int cols = data.length;
        int rows = data[0].length;
        int[][] newdata = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newdata[i][j] = data[cols - j - 1][i];
            }
        }

        correctBlockPosition(newdata);
        calcDisplacement(newdata);

        return newdata;
    }

    protected int[][] tiltLeft(int[][] data) {
        int cols = data.length;
        int rows = data[0].length;
        int[][] newdata = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newdata[i][j] = data[j][rows - i - 1];
            }
        }

        correctBlockPosition(newdata);
        calcDisplacement(newdata);

        return newdata;
    }

    private void correctBlockPosition(int[][] data) {
        blockWidth = data[0].length;
        blockHeight = data.length;
        int pos = data[0].length + position.getPosX();
        if (pos > TetrisField.COLS) {
            int diff = (pos - TetrisField.COLS);
            position = position.substract(diff, 0);
            topLeft = topLeft.subtract(MainController.CELL_WIDTH * diff, 0);
        }
    }

    protected boolean isNotOverlap(int[][] blockdata, List<List<Cell>> field, GridPosition position) {
        for (int row = 0; row < blockdata.length; row++) {
            for (int col = 0; col < blockdata[row].length; col++) {
                if(blockdata[row][col] == 1) {
                    int absRow = row + position.getPosY();
                    int absCol = col + position.getPosX();
                    if (absCol < field.get(0).size() && absRow < field.size()) {
                        if (field.get(absRow).get(absCol).isFilled()) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected boolean isNotBehindLeft() {
        return (position.getPosX() > 0);
    }

    protected boolean isNotAfterRight() {
        return (position.getPosX() < (TetrisField.COLS - blockWidth));
    }

    public abstract int[][] getData();

    public int[] getBlockDisplacementVec() {
        return displacementVec;
    }

    public void setBlockDisplacementVec(int[] displacementVec) {
        this.displacementVec = displacementVec;
    }

    /**
     * Calculate the how much empty space does a block need to be well placed.
     * Example for TBlock:
     *      ###
     *       #
     *
     * The displacement vector should looks like: [1 0 1]
     *
     * @param blockData block indices matrix
     */
    protected void calcDisplacement(int[][] blockData) {
        displacementVec = new int[blockData[0].length];
        boolean[] blockedColsVec = new boolean[blockData[0].length];

        for (int row = blockData.length - 1; row >= 0; row--) {
            for (int col = 0; col < blockData[row].length; col++) {
                if (blockData[row][col] != 1) {
                    if (!blockedColsVec[col]) {
                        displacementVec[col]++;
                    }
                } else {
                    blockedColsVec[col] = true;
                }
            }
        }
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point2D getTopLeft() {
        return this.topLeft;
    }

    public GridPosition getGridposition() {
        return this.position;
    }

    public void setGridposition(GridPosition position) {
        this.position = position;
    }

    public void setTopLeft(Point2D newTopLeft) {
        this.topLeft = newTopLeft;
    }

    public void moveLeft(double delta) {
        topLeft = topLeft.subtract(delta, 0);
        position = position.substract(1, 0);
    }

    public void moveRight(double delta) {
        topLeft = topLeft.add(delta, 0);
        position = position.addition(1, 0);
    }

    public void moveDown(double delta) {
        if ((position.getPosY() + blockHeight) < TetrisField.ROWS) {
            topLeft = topLeft.add(0, delta);
            position = position.addition(0, 1);
        }
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public void setBlockWidth(int blockWidth) {
        this.blockWidth = blockWidth;
    }

    protected int[][] deepGridDataCopy(int[][] data) {
        return Arrays.stream(data).map(el -> el.clone()).toArray($ -> data.clone());
    }

    protected int[] deepArrayCopy(int[] data) {
        return Arrays.copyOf(data, data.length);
    }

    public abstract void rotateLeft();

    public abstract void rotateRight();

    public abstract boolean isRotateLeftAllowed(List<List<Cell>> field);

    public abstract boolean isRotateRightAllowed(List<List<Cell>> field);

    public abstract boolean isMoveLeftAllowed(List<List<Cell>> field);

    public abstract boolean isMoveRightAllowed(List<List<Cell>> field);

    public abstract int getBlockWidth();

    public abstract int getBlockHeight();

    public abstract BlockType getBlockType();

    public abstract Block clone();
}