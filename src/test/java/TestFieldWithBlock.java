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

    public final static int DEFAULT_COLS = 20;
    public final static int DEFAULT_ROWS = 40;

    private TetrisField scope;

    @Test
    public void shouldIntegrateBlock() {
        scope = new TetrisField(DEFAULT_COLS, DEFAULT_ROWS);

        for (int i = 0; i < scope.getCols(); i++) {
            scope.setCell(scope.getRows() - 1, i, Cell.builder().filled(true)
                    .color(Color.RED).build());
        }

        int middle = DEFAULT_COLS / 3;
        Block block = new Lblock(Color.MAGENTA);
        scope.placeBlock(block);
        log.info(scope.toString());
        assertEquals(true, scope.getField().get(1).get(middle).isFilled());
    }

    @Test
    public void shouldDetectGroundCollision() {
        scope = new TetrisField(DEFAULT_COLS, DEFAULT_ROWS);
        int middleCol = DEFAULT_COLS / 3;
        int lastRow = DEFAULT_ROWS;
        Block block = new Lblock(Color.MAGENTA);
        block.setTopLeft(new Point2D(middleCol, lastRow - 1));
        block.setGridposition(new GridPosition(middleCol, 38));
        CollisionType ctype = scope.detectFutureCollision(block);
        assertEquals(CollisionType.GROUND_BELOW, ctype);
    }

}
