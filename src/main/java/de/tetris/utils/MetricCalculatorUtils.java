package de.tetris.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetricCalculatorUtils {

    public static int[] calcMetricBrickFieldLevelDiffVec(int[] brickLevelVec) {
        if (brickLevelVec.length == 0) {
            throw new RuntimeException("Brick level vector size is empty");
        }

        if (brickLevelVec.length == 1) {
            throw new RuntimeException("Brick level vector size should be bigger than 1");
        }

        return calcMetricDiff(brickLevelVec);
    }

    public static int[] calcMetricBrickDisplDiffVec(int[] brickDisplacement) {
        if (brickDisplacement.length == 0) {
            throw new RuntimeException("Brick displacement vector size should be bigger than 0");
        }
        // special case if the block is a I Block
        if (brickDisplacement.length == 1) {
            return new int[]{4};
        }
        return calcMetricDiff(brickDisplacement);
    }

    private static int[] calcMetricDiff(int[] brickLevelVec) {
        int[] metricVec = new int[brickLevelVec.length - 1];
        for (int i = 0; i < metricVec.length; i++) {
            metricVec[i] = brickLevelVec[i] - brickLevelVec[i + 1];
        }
        return metricVec;
    }


    public static int[] calcMetricBrickFieldLevelExtDiffVec(int[] brickLevelVec) {
        int[] metricVec = new int[(brickLevelVec.length * 2) - 2];
        int resultIndex = 0;
        for (int i = 0; i < brickLevelVec.length - 1; i++) {
            metricVec[resultIndex] = brickLevelVec[i] - brickLevelVec[i + 1];
            metricVec[resultIndex + 1] = brickLevelVec[i + 1] - brickLevelVec[i];
            resultIndex += 2;
        }
        return metricVec;
    }

    public static List<Integer> calcOptimumPlacePositions(int[] metricBrickFieldLevelDiffVec,
                                                          int[] metricBrickDisplacementDiffVec) {
        int minDiff = calcBestPlaceDiff(metricBrickFieldLevelDiffVec, metricBrickDisplacementDiffVec);
        List<Integer> optimalPositions = new ArrayList<>();

        for (int i = 0; i < metricBrickFieldLevelDiffVec.length - (metricBrickDisplacementDiffVec.length - 1); i++) {
            int sumOfDiff = 0;
            for (int j = 0; j < metricBrickDisplacementDiffVec.length; j++) {
                sumOfDiff += Math.abs(metricBrickFieldLevelDiffVec[i + j] - metricBrickDisplacementDiffVec[j]);
            }
            if (minDiff == sumOfDiff) {
                optimalPositions.add(i);
            }
        }
        return optimalPositions;
    }

    public static List<Integer> calcOptimumPlacePosVerBlockI(int[] metricBrickFieldLevelDiffVec) {
        int minValue = calcBestPlacePositionForVerticalBlockI(metricBrickFieldLevelDiffVec);
        Set<Integer> optimalPositions = new HashSet<>();
        int pos = 0;
        for (int i = 0; i < metricBrickFieldLevelDiffVec.length - 1; i += 2) {
            if (minValue == metricBrickFieldLevelDiffVec[i]) {
                optimalPositions.add(pos);
            }
            if (minValue == metricBrickFieldLevelDiffVec[i + 1]) {
                optimalPositions.add(pos + 1);
            }
            pos++;
        }
        return new ArrayList<>(optimalPositions);
    }

    private static int calcBestPlaceDiff(int[] metricBrickFieldLevelDiffVec,
                                         int[] metricBrickDisplacementDiffVec) {
        int minDiff = Integer.MAX_VALUE;
        for (int i = 0; i < metricBrickFieldLevelDiffVec.length - (metricBrickDisplacementDiffVec.length - 1); i++) {
            int sumOfDiff = 0;
            for (int j = 0; j < metricBrickDisplacementDiffVec.length; j++) {

                sumOfDiff += Math.abs(metricBrickFieldLevelDiffVec[i + j] - metricBrickDisplacementDiffVec[j]);
            }
            if (minDiff > sumOfDiff) {
                minDiff = sumOfDiff;
            }
        }
        return minDiff;
    }

    /**
     * Special case for the vertical block I, because it is the only one which have one column. Other
     * blocks have at least 2 or more columns.
     * @param metricBrickFieldLevelDiffVec calculated brick level vector
     * @return best place metric value
     */
    private static int calcBestPlacePositionForVerticalBlockI(int[] metricBrickFieldLevelDiffVec) {
        int min = Integer.MAX_VALUE;
        for (int value : metricBrickFieldLevelDiffVec) {
            if (min > value) {
                min = value;
            }
        }
        return min;
    }
}