import de.tetris.model.block.Block;
import de.tetris.model.block.Jblock;
import de.tetris.model.block.Lblock;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Lam
 */
@Slf4j
public class TestMatrixRotation {

    Block block;

    @Test
    public void shouldRotateLeft() {
        block = new Jblock(new Color(0.5, 0.5, 0.5, 1));
        log.info(block.toString());
        block.rotateLeft();
        log.info(block.toString());
        assertEquals(3, block.getData().length);
        assertEquals(2, block.getData()[0].length);
    }

    @Test
    public void shouldRotateRight() {
        block = new Lblock(new Color(0.5, 0.5, 0.5, 1));
        log.info(block.toString());
        block.rotateRight();
        log.info(block.toString());
        assertEquals(1, block.getData()[0][0]);
        assertEquals(1, block.getData()[2][1]);
    }

}
