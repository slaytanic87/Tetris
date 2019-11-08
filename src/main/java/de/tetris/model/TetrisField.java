package de.tetris.model;

import de.tetris.model.block.Block;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Lam
 */
@Slf4j
public class TetrisField {

    private static final int SPEEDSTEPINMILLIS = 40;
    public static long INITIAL_LOWEST_SPEED_IN_MILLI;

    private LinkedBlockingQueue<Block> queue;

    private Block currentBlock;

    public static int COLS = 0;
    public static int ROWS = 0;

    private List<List<Cell>> fieldData = null;

    private int[] brickLevelVec;

    public TetrisField(int cols, int rows) {
        COLS = cols;
        ROWS = rows;
        log.debug("Cols: {} Rows: {}", cols, rows);
        initBrickLevelVec(cols);
        resetModel();
    }

    public void resetModel() {
        fieldData = new ArrayList<List<Cell>>(ROWS);
        for (int i = 0; i < ROWS; i++) {
            addEmptyRow();
        }
        resetBrickLevelVec();
    }

    public void setCurrentBlock(Block block) {
        currentBlock = block;
    }

    public Block getCurrentBlock() {
        return currentBlock;
    }

    public int getNumberCols() {
        return COLS;
    }

    public int getNumberRows() {
        return ROWS;
    }

    public long calcSpeed() {
        long newSpeed = INITIAL_LOWEST_SPEED_IN_MILLI - TimeUnit.MILLISECONDS
                .toNanos(SPEEDSTEPINMILLIS * Scores.getInstance().getLevel());
        return (newSpeed < 0) ? 0 : newSpeed;
    }

    public LinkedBlockingQueue<Block> getQueue() {
        return this.queue;
    }

    public void setQueue(LinkedBlockingQueue<Block> queue) {
        this.queue = queue;
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


    public CollisionType detectCollisionInAdvance(Block block) {
        int[][] blockData = block.getData();
        GridPosition blockPosTopLeft = new GridPosition(block.getGridposition().getPosX(),
                block.getGridposition().getPosY() + 1);
        return determineCollisionType(blockData, blockPosTopLeft);
    }

    public CollisionType determineCollision(Block block) {
        return determineCollisionType(block.getData(), block.getGridposition());
    }

    private CollisionType determineCollisionType(int[][] blockData, GridPosition blockGridPosTopLeft) {
        for (int row = 0; row < blockData.length; row++) {
            for (int col = 0; col < blockData[row].length; col++) {
                int currentCol = col + blockGridPosTopLeft.getPosX();
                int currentRow = row + blockGridPosTopLeft.getPosY();
                if (blockData[row][col] == 1) {
                    if (currentRow >= fieldData.size()) {
                        return CollisionType.GROUND_BELOW;
                    }
                    if (fieldData.get(1).get(currentCol).isFilled()) {
                        return CollisionType.BLOCK_OVERFLOW;
                    }

                    if (col == blockData[0].length - 1 && row < blockData.length - 1) {
                        if (fieldData.get(currentRow).get(currentCol).isFilled()) {
                            return CollisionType.BLOCK_RIGHT;
                        }
                    } else if (col == 0 && row < blockData.length - 1) {
                        if (fieldData.get(currentRow).get(currentCol).isFilled()) {
                            return CollisionType.BLOCK_LEFT;
                        }
                    }
                    if (fieldData.get(currentRow).get(currentCol).isFilled()) {
                        log.debug("Block below current row {} col {}", currentRow, currentCol);
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
        GridPosition topLeftPos = block.getGridposition().clone();
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

    public void decreaseBrickLevelVec(int processedRows) {
        if (brickLevelVec == null || brickLevelVec.length < COLS) {
            throw new RuntimeException("brickLevelVec size should equals the number of cols!");
        }
        for (int i = 0; i < brickLevelVec.length; i++) {
            int value = brickLevelVec[i] - processedRows;
            if (value < 0) {
                throw new RuntimeException("brickLevel at " + i + " cannot not be less than 0!");
            }
            brickLevelVec[i] = value;
        }
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

    public List<List<String>> getFieldAsColorCells() {
        List<List<String>> coloredField = new LinkedList<>();
        for (List<Cell> row: fieldData) {
            List<String> coloredRow = new LinkedList<>();
            for (Cell cell: row) {
                if (cell.isFilled()) {
                    coloredRow.add(colorToHtmlHex(cell.getColor()));
                } else {
                    coloredRow.add("#000000");
                }
            }
            coloredField.add(coloredRow);
        }
        return coloredField;
    }

    private void initBrickLevelVec(int numberOfCols) {
        brickLevelVec = new int[numberOfCols];
    }

    private void resetBrickLevelVec() {
        for (short i = 0; i < brickLevelVec.length; i++) {
            brickLevelVec[i] = 0;
        }
    }

    public void updateBrickLevel(Block block) {
        int colTopLeftX = block.getGridposition().getPosX();
        int rowTopLeftY = block.getGridposition().getPosY();
        int[][] data = block.getData();
        for (short row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                if (data[row][col] == 1) {
                    int currentCol = col + colTopLeftX;
                    int currentRow = TetrisField.ROWS - (row + rowTopLeftY);
                    int currentWaterLevelPos = getBrickLevelVecAtPos(currentCol);
                    if (currentWaterLevelPos < currentRow) {
                        setBrickLevelVecAtPos(currentCol, currentRow);
                    }
                }
            }
        }
    }

    private void setBrickLevelVecAtPos(int index, int value) {
        checkBrickLevelIndexAndThrow(index);
        brickLevelVec[index] = value;
    }

    private int getBrickLevelVecAtPos(int index) {
        checkBrickLevelIndexAndThrow(index);
        return brickLevelVec[index];
    }

    public int[] getBrickLevelVec() {
        return brickLevelVec;
    }

    private void checkBrickLevelIndexAndThrow(int index) {
        if (index > brickLevelVec.length - 1 ) {
            throw new RuntimeException("Index should be less equal number of columns");
        }
        if (index < 0) {
            throw new RuntimeException("Index should be bigger or equal to 0");
        }
    }

    private String colorToHtmlHex(Color color) {
        int red = (int) color.getRed() * 255;
        int green = (int) color.getGreen() * 255;
        int blue = (int) color.getBlue() * 255;
        return "#" + String.format("%02X", red) + String.format("%02X", green) + String.format("%02X", blue);
    }
}
