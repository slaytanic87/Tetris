package de.tetris.model;

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
    public static long INITIAL_LOWEST_SPEED_IN_MILLI;

    public static int COLS = 0;
    public static int ROWS = 0;

    private List<List<Cell>> fieldData = null;

    public TetrisField(int cols, int rows) {
        COLS = cols;
        ROWS = rows;
        log.debug("Cols: {} Rows: {}", cols, rows);
        resetModel();
    }

    public void resetModel() {
        fieldData = new ArrayList<List<Cell>>(ROWS);
        for (int i = 0; i < ROWS; i++) {
            addEmptyRow();
        }
    }

    public int getNumberCols() {
        return COLS;
    }

    public int getNumberRows() {
        return ROWS;
    }

    public long calcSpeed() {
        long newSpeed = INITIAL_LOWEST_SPEED_IN_MILLI - TimeUnit.MILLISECONDS
                .toNanos(SPEEDSTEPINMILLIS * Scores.INSTANCE.getLevel());
        return (newSpeed < 0) ? 0 : newSpeed;
    }

    public void addEmptyRow() {
        ArrayList<Cell> row = new ArrayList<>(COLS);
        for (int i = 0; i < COLS; i++) {
            row.add(i, new Cell());
        }
        this.fieldData.add(0, row);
    }

    public Cell getCell(int rowIndex, int colIndex) {
        return this.fieldData.get(rowIndex).get(colIndex);
    }

    public void setCell(int rowIndex, int colIndex, Cell cell) {
        this.fieldData.get(rowIndex).set(colIndex, cell);
    }

    public void removeRow(int index) {
        this.fieldData.remove(index);
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
                    if (currentRow >= fieldData.size()) {
                        return CollisionType.GROUND_BELOW;
                    }
                    if (fieldData.get(1).get(currentCol).isFilled()) {
                        return CollisionType.BLOCK_OVERFLOW;
                    }
                    if (fieldData.get(currentRow).get(currentCol).isFilled()) {
                        log.debug("Block below");
                        return CollisionType.BLOCK_BELOW;
                    }
                }
            }
        }
        return CollisionType.NONE;
    }


    /**
     * Copy block grid data into tetris fieldData grid data.
     * @param block {@link Block}
     */
    public void placeBlock(Block block) {
        int[][] blockdata = block.getData();
        GridPosition topLeftPos = block.getGridposition();
        for (int row = 0; row < blockdata.length; row++) {
            int c = 0;
            for(int col = 0; col < blockdata[row].length; col++) {
                if (blockdata[row][col] == 1) {
                    if (topLeftPos.getPosY() < this.getNumberRows() && topLeftPos.getPosX() < this.getNumberCols()) {
                        fieldData.get(topLeftPos.getPosY()).get(topLeftPos.getPosX()).setFilled(true);
                        fieldData.get(topLeftPos.getPosY()).get(topLeftPos.getPosX()).setColor(block.getColor());
                    }
                }
                topLeftPos = topLeftPos.addition(1, 0);
                c++;
            }
            topLeftPos.substract(c, 0);
            topLeftPos = topLeftPos.addition(0, 1);
        }
        log.debug("block placed to fieldData model");
    }

    /**
     * Detect wether a row is complete filled.
     * @param rowPos index position of a row
     * @return is filled?
     */
    public boolean isRowFilled(int rowPos) {
        boolean isRowFilled;
        rowPos = rowPos < 0 ? 0 : rowPos;
        if (rowPos < fieldData.size()) {
            List<Cell> row = fieldData.get(rowPos);
            isRowFilled = row.get(0).isFilled();
            for (int i = 1; i < row.size(); i++) {
                isRowFilled = isRowFilled && row.get(i).isFilled();
            }
        } else {
            throw new IndexOutOfBoundsException("Row position is out of bounds");
        }
        return isRowFilled;
    }

    public GridPosition getFirstFilledCell(int colPos) {
        colPos = colPos < 0 ? 0: colPos;
        if (colPos < COLS) {
            for (int row = 0; row < ROWS; row++) {
                Cell cell = getCell(row, colPos);
                if (cell.isFilled()) {
                    return GridPosition.builder().posY(row).posX(colPos)
                            .build();
                }
            }
        } else {
            throw new IndexOutOfBoundsException("Column position is out of bounds");
        }
        return null;
    }

    public int processFilledRows() {
        int processedRows = 0;
        for (int row = 0; row < fieldData.size(); row++) {
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
        return this.fieldData;
    }

    public String toString() {
        String str = "\n";

        for (List<Cell> row: fieldData) {
            for (Cell cell: row) {
                str += cell.isFilled() ? " 1 " : " 0 ";
            }
            str += "\n";
        }
        return str;
    }

}
