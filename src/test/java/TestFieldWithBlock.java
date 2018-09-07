import de.tetris.model.Cell;
import de.tetris.model.CollisionType;
import de.tetris.model.GridPosition;
import de.tetris.model.TetrisField;
import de.tetris.model.block.Block;
import de.tetris.model.block.Lblock;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Lam
 */
@Slf4j
public class TestFieldWithBlock {

    private final static int DEFAULT_COLS = 20;
    private final static int DEFAULT_ROWS = 40;

    private TetrisField scope;

    @Test
    public void shouldIntegrateBlock() {
        scope = new TetrisField(DEFAULT_COLS, DEFAULT_ROWS);

        for (int i = 0; i < scope.getNumberCols(); i++) {
            scope.setCell(scope.getNumberRows() - 1, i, Cell.builder().filled(true)
                    .color(Color.RED).build());
        }

        int middle = DEFAULT_COLS / 2;
        Block block = new Lblock(Color.MAGENTA);
        scope.placeBlock(block);
        log.info(scope.toString());
        assertEquals(true, scope.getField().get(1).get(middle).isFilled());
    }

    @Test
    public void shouldDetectGroundCollision() {
        scope = new TetrisField(DEFAULT_COLS, DEFAULT_ROWS);
        int middleCol = DEFAULT_COLS / 3;
        Block block = new Lblock(Color.MAGENTA);
        block.setTopLeft(new Point2D(middleCol, DEFAULT_ROWS - 1));
        block.setGridposition(new GridPosition(middleCol, 38));
        CollisionType ctype = scope.detectCollisionInAdvance(block);
        assertEquals(CollisionType.GROUND_BELOW, ctype);
    }

    @Test
    public void shouldDetectFullFilledRows() {
        scope = new TetrisField(DEFAULT_COLS, DEFAULT_ROWS);
        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < DEFAULT_COLS; col++) {
                Cell cell = new Cell();
                cell.setFilled(true);
                scope.setCell(row, col, cell);
            }
        }
        boolean isFilled = scope.isRowFilled(1);
        assertEquals(true, isFilled);
        isFilled = scope.isRowFilled(2);
        assertEquals(true, isFilled);
        isFilled = scope.isRowFilled(3);
        assertEquals(true, isFilled);
        isFilled = scope.isRowFilled(4);
        assertEquals(false, isFilled);
    }

    @Test
    public void shouldProcessFilledRows() {
        scope = new TetrisField(DEFAULT_COLS, DEFAULT_ROWS);
        for (int row = 1; row < 5; row++) {
            for (int col = 0; col < DEFAULT_COLS; col++) {
                Cell cell = new Cell();
                cell.setFilled(true);
                scope.setCell(row, col, cell);
            }
        }
        boolean isFilled = scope.isRowFilled(1);
        assertEquals(true, isFilled);
        isFilled = scope.isRowFilled(2);
        assertEquals(true, isFilled);
        isFilled = scope.isRowFilled(3);
        assertEquals(true, isFilled);
        isFilled = scope.isRowFilled(4);
        assertEquals(true, isFilled);
        scope.processFilledRows();
        isFilled = scope.isRowFilled(1);
        assertEquals(false, isFilled);
        isFilled = scope.isRowFilled(2);
        assertEquals(false, isFilled);
        isFilled = scope.isRowFilled(3);
        assertEquals(false, isFilled);
        isFilled = scope.isRowFilled(4);
        assertEquals(false, isFilled);
    }

    @Test
    public void shouldNotProcessUnfilledRows() {
        scope = new TetrisField(DEFAULT_COLS, DEFAULT_ROWS);
        for (int col = 0; col < DEFAULT_COLS - 1; col++) {
            Cell cell = new Cell();
            cell.setFilled(true);
            scope.setCell(3, col, cell);
        }
        boolean isFilled = scope.isRowFilled(3);
        assertEquals(false, isFilled);
        scope.processFilledRows();
        isFilled = scope.isRowFilled(3);
        assertEquals(false, isFilled);
    }
}
