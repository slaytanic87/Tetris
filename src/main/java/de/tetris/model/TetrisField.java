package de.tetris.model;

import de.tetris.controller.game.Scores;
import de.tetris.model.block.Block;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lam
 */
@Slf4j
public class TetrisField {

    private static final int SPEEDSTEPINMILLIS = 40;
    public static long INITIALSPEEDINMILLI;

    public static int COLS = 0;
    public static int ROWS = 0;

    private List<List<Cell>> field = null;

    public TetrisField(int cols, int rows) {
        COLS = cols;
        ROWS = rows;
        log.debug("Cols: {} Rows: {}", cols, rows);
        resetModel();
    }

    public void resetModel() {
        field = new ArrayList<List<Cell>>(ROWS);
        for (int i = 0; i < ROWS; i++) {
            addEmptyRow();
        }
    }

    public int getCols() {
        return COLS;
    }

    public int getRows() {
        return ROWS;
    }

    public long calcSpeed() {
        long newSpeed = INITIALSPEEDINMILLI - TimeUnit.MILLISECONDS
                .toNanos(SPEEDSTEPINMILLIS * Scores.INSTANCE.getLevel());
        return (newSpeed < 0) ? 0 : newSpeed;
    }

    public void addEmptyRow() {
        ArrayList<Cell> row = new ArrayList<>(COLS);
        for (int i = 0; i < COLS; i++) {
            row.add(i, new Cell());
        }
        this.field.add(0, row);
    }

    public Cell getCell(int rowIndex, int colIndex) {
        return this.field.get(rowIndex).get(colIndex);
    }

    public void setCell(int rowIndex, int colIndex, Cell cell) {
        this.field.get(rowIndex).set(colIndex, cell);
    }

    public void removeRow(int index) {
        this.field.remove(index);
    }


    public CollisionType detectFutureCollision(Block block) {
        int[][] blockData = block.getData();
        GridPosition blockPosTopLeft = new GridPosition(block.getGridposition().getPosX(),
                block.getGridposition().getPosY() + 1);
        for (int row = 0; row < blockData.length; row++) {
            for (int col = 0; col < blockData[row].length; col++) {
                int currentCol = col + blockPosTopLeft.getPosX();
                int currentRow = row + blockPosTopLeft.getPosY();
                if (blockData[row][col] == 1) {
                    if (currentRow >= field.size()) {
                        return CollisionType.GROUND_BELOW;
                    }
                    if (field.get(1).get(currentCol).isFilled()) {
                        return CollisionType.BLOCK_OVERFLOW;
                    }
                    if (field.get(currentRow).get(currentCol).isFilled()) {
                        log.debug("Block below");
                        return CollisionType.BLOCK_BELOW;
                    }
                }
            }
        }
        return CollisionType.NONE;
    }


    /**
     * Copy block grid data into tetris field grid data.
     * @param block {@link Block}
     */
    public void placeBlock(Block block) {
        int[][] blockdata = block.getData();
        GridPosition topLeftPos = block.getGridposition();
        for (int row = 0; row < blockdata.length; row++) {
            int c = 0;
            for(int col = 0; col < blockdata[row].length; col++) {
                if (blockdata[row][col] == 1) {
                    if (topLeftPos.getPosY() < this.getRows() && topLeftPos.getPosX() < this.getCols()) {
                        field.get(topLeftPos.getPosY()).get(topLeftPos.getPosX()).setFilled(true);
                        field.get(topLeftPos.getPosY()).get(topLeftPos.getPosX()).setColor(block.getColor());
                    }
                }
                topLeftPos = topLeftPos.addition(1, 0);
                c++;
            }
            topLeftPos.substract(c, 0);
            topLeftPos = topLeftPos.addition(0, 1);
        }
        log.debug("block placed to field model");
    }

    /**
     * Detect wether a row is complete filled.
     * @param rowPos index position of a row
     * @return is filled?
     */
    public boolean isRowFilled(int rowPos) {
        boolean isRowFilled;
        if (rowPos < field.size()) {
            List<Cell> row = field.get(rowPos);
            isRowFilled = row.get(0).isFilled();
            for (int i = 1; i < row.size(); i++) {
                isRowFilled = isRowFilled && row.get(i).isFilled();
            }
        } else {
            throw new RuntimeException("Row position is out of bound");
        }
        return isRowFilled;
    }

    public int processFilledRows() {
        int processedRows = 0;
        for (int row = 0; row < field.size(); row++) {
            boolean isfilled = isRowFilled(row);
            if (isfilled) {
                removeRow(row);
                addEmptyRow();
                processedRows++;
                log.debug("Removed row: {}", row);
            }
        }
        return processedRows;
    }

    public List<List<Cell>> getField() {
        return this.field;
    }

    public String toString() {
        String str = "\n";

        for (List<Cell> row: field) {
            for (Cell cell: row) {
                str += cell.isFilled() ? " 1 " : " 0 ";
            }
            str += "\n";
        }
        return str;
    }

}
