import de.tetris.model.Cell;
import de.tetris.model.TetrisField;
import de.tetris.model.block.*;
import de.tetris.utils.MetricCalculatorUtils;
import javafx.scene.paint.Color;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;


public class TestMetricCalculation {


    @Test
    public void shouldRotateAndUpdateBlockDisplacementVecCorrectly() {
        // given

        /* #
         *###
         *000
         */
        Tblock blockT = new Tblock(Color.AQUA);

        /*  ##
         * ##
         * 001
         */
        Sblock blockS = new Sblock(Color.AQUA);

        /* ####
         * 0000
         */
        Iblock blockI = new Iblock(Color.AQUA);

        /*   #
         * ###
         * 000
         */
        Lblock blockL = new Lblock(Color.AQUA);

        /* #
         * ###
         * 000
         */
        Jblock blockJ = new Jblock(Color.AQUA);

        // when
        blockT.rotateRight();
        blockS.rotateRight();
        blockI.rotateRight();
        blockL.rotateLeft();
        blockJ.rotateRight();

        // then
        Assertions.assertThat(blockT.getBlockDisplacementVec()).isEqualTo(new int[]{0, 1});
        Assertions.assertThat(blockS.getBlockDisplacementVec()).isEqualTo(new int[]{1, 0});
        Assertions.assertThat(blockI.getBlockDisplacementVec()).isEqualTo(new int[]{0});
        Assertions.assertThat(blockL.getBlockDisplacementVec()).isEqualTo(new int[]{2, 0});
        Assertions.assertThat(blockJ.getBlockDisplacementVec()).isEqualTo(new int[]{0, 2});
    }

    @Test
    public void shouldCalcBrickLevelMetricDiffVec() {
        // given

        /*     #
         * #   # #
         * # # # # #
         * # # # # # _
         * 3 2 4 3 2 0
         */
        int[] brickLevelVec = {3, 2, 4, 3, 2, 0};

        // when
        int[] metricBrickLevelDiffVec = MetricCalculatorUtils.calcMetricBrickFieldLevelDiffVec(brickLevelVec);

        // then
        Assertions.assertThat(metricBrickLevelDiffVec).isEqualTo(new int[]{1, -2, 1, 1, 2});
    }

    @Test
    public void shouldCalcBrickLevelMetricDiffTwoWayDiffVec() {
        // given

        /*     #   #
         *     #   #
         * #   #   #
         * #   #   #
         * #   # # #
         * 3 0 5 1 5 0
         */
        final int[] brickLevelVec = {3, 0, 5, 1, 5, 0};

        // when
        final int[] resultMetricVec = MetricCalculatorUtils.calcMetricBrickFieldLevelExtDiffVec(brickLevelVec);

        // then
        final int maxMetricVecLength = (brickLevelVec.length * 2) - 2;
        Assertions.assertThat(resultMetricVec.length).isEqualTo(maxMetricVecLength);
        Assertions.assertThat(resultMetricVec).isEqualTo(new int[] {3,-3, -5,5, 4,-4, -4,4, 5,-5});
    }

    @Test
    public void shouldCalcBrickDisplacementDiffVec() {
        // given

        /*  # #
         *    #
         *    #
         *  2 0
         */
        final int[] brickDisplVecForBlockL = {2, 0};

        /* #
         * #
         * #
         * #
         * 4 => special case for block I only
         */
        final int[] brickDisplVecForBlockI = {0};

        /*   # #
         * # #
         * 0 0 1
         */
        final int[] brickDisplVecForBlockS = {0, 0, 1};

        // when
        int[] metricBrickDisplDiffVecL = MetricCalculatorUtils.calcMetricBrickDisplDiffVec(brickDisplVecForBlockL);
        int[] metricBrickDisplDiffVecI = MetricCalculatorUtils.calcMetricBrickDisplDiffVec(brickDisplVecForBlockI);
        int[] metricBrickDisplDiffVecS = MetricCalculatorUtils.calcMetricBrickDisplDiffVec(brickDisplVecForBlockS);

        // then
        Assertions.assertThat(metricBrickDisplDiffVecL).isEqualTo(new int[]{2});
        Assertions.assertThat(metricBrickDisplDiffVecI).isEqualTo(new int[]{4});
        Assertions.assertThat(metricBrickDisplDiffVecS).isEqualTo(new int[] {0, -1});
    }

    @Test
    public void shouldCalcOptimumPlacePosition() {
        // given

        /*     #
         * #   # #
         * # # # # #
         * # # # # # _
         * 3 2 4 3 2 0
         */
        final int[] metricBrickLevelVec = {1, -2, 1, 1, 2};

        /*  # #
         *    #
         *    #
         *  2 0
         */
        final int[] metricBrickDisplacementVecForLblock = {2};

        /* #
         * # #
         *   #
         * 1 0
         */
        final int[] metricBrickDisplacementVecForSblock = {1};


        /* # #
         * #
         * #
         * 0 2
         */
        final int[] metricBrickDisplacementVecForJblock = {-2};

        // when
        List<Integer> positionForL = MetricCalculatorUtils.calcOptimumPlacePositions(metricBrickLevelVec,
                metricBrickDisplacementVecForLblock);
        List<Integer> positionForS = MetricCalculatorUtils.calcOptimumPlacePositions(metricBrickLevelVec,
                metricBrickDisplacementVecForSblock);
        List<Integer> positionForJ = MetricCalculatorUtils.calcOptimumPlacePositions(metricBrickLevelVec,
                metricBrickDisplacementVecForJblock);

        // then
        Assertions.assertThat(positionForL.get(0)).isEqualTo(4);
        Assertions.assertThat(positionForS.size()).isEqualTo(3);
        Assertions.assertThat(positionForS.get(0)).isEqualTo(0);
        Assertions.assertThat(positionForJ.get(0)).isEqualTo(1);
    }

    @Test
    public void shouldCalcOptimumPlacePositionForVerticalBlockI() {
        // given

        /*      #
         *      #
         *      #   #
         *      #   #
         *  #   # # #
         *  #   # # #
         *  #   # # #
         *  3 0 7 3 5 0
         */
        final int[] metricBrickLevelVec = {3, -3, -7, 7, 4, -4, -2, 2, 5, -5};

        final int[] metricEmptyBrickLevelVec = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        /*  #   #
         *  #   #   #
         *  #   #   #
         *  #   #   #
         *  #   #   #
         *  #   #   #
         *  6 0 6 0 5 0
         */
        final int[] metricBrickLevelVec2 = {6, -6, -6, 6, 6, -6, -5, 5, 5, -5};


        // when
        List<Integer> positionForI = MetricCalculatorUtils.calcOptimumPlacePosVerBlockI(metricBrickLevelVec);
        List<Integer> positionForI2 = MetricCalculatorUtils.calcOptimumPlacePosVerBlockI(metricBrickLevelVec2);
        List<Integer> positionForIWhenEmpty = MetricCalculatorUtils
                .calcOptimumPlacePosVerBlockI(metricEmptyBrickLevelVec);

        // then
        Assertions.assertThat(positionForI.size()).isEqualTo(1);
        Assertions.assertThat(positionForI.get(0)).isEqualTo(1);

        Assertions.assertThat(positionForIWhenEmpty.size()).isEqualTo(6);
        Assertions.assertThat(positionForIWhenEmpty.get(0)).isEqualTo(0);
        Assertions.assertThat(positionForIWhenEmpty.get(positionForIWhenEmpty.size() - 1)).isEqualTo(5);

        Assertions.assertThat(positionForI2.size()).isEqualTo(2);
        Assertions.assertThat(positionForI2.toArray(new Integer[positionForI2.size()])).isEqualTo(new int[]{1, 3});
    }

    @Test
    public void shouldDecreaseFieldBrickLevelVec() {
        // given
        int defaultCols = 5;
        int defaultRows = 5;
        /*
         *
         *    # #   #
         *  # # # # #
         *  1 2 2 1 2
         */
        TetrisField tetrisField = new TetrisField(defaultCols, defaultRows);
        for (int row = 0; row < 1; row++) {
            for (int col = 0; col < defaultCols; col++) {
                Cell cell = new Cell();
                cell.setFilled(true);
                tetrisField.setCell(row, col, cell);
            }
        }

        for (int i = 0; i < defaultCols; i++) {
            tetrisField.getBrickLevelVec()[i] = 1;
        }

        tetrisField.getBrickLevelVec()[1] += 1;
        tetrisField.getBrickLevelVec()[2] += 1;
        tetrisField.getBrickLevelVec()[4] += 1;

        tetrisField.setCell(1, 1, Cell.builder().filled(true).build());
        tetrisField.setCell(1, 2, Cell.builder().filled(true).build());
        tetrisField.setCell(1, 4, Cell.builder().filled(true).build());

        // when
        int processed = tetrisField.processFilledRows();
        tetrisField.decreaseBrickLevelVec(processed);

        // then
        /*
         *
         *    # #   #
         *  0 1 1 0 1
         */
        int[] brickLevelVec = tetrisField.getBrickLevelVec();
        Assertions.assertThat(brickLevelVec.length).isEqualTo(5);
        Assertions.assertThat(brickLevelVec[1]).isEqualTo(1);
        Assertions.assertThat(brickLevelVec[2]).isEqualTo(1);
        Assertions.assertThat(brickLevelVec[4]).isEqualTo(1);
    }
}