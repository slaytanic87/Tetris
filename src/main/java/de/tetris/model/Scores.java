package de.tetris.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * @author Lam
 */
@Slf4j
@Data
public class Scores {

    public static Scores INSTANCE = new Scores();

    private static final int ROWPOINT = 100;
    private static final int LEVELUP = 8;

    private BigDecimal mainscore;

    private double currentSpeedRate;

    private int level;

    private int finishRows;

    private Scores() {
        resetScores();
    }

    public void addScore(int finishedRows) {
        mainscore = mainscore.add(new BigDecimal(finishedRows * ROWPOINT));
        log.debug("Score update: {}", mainscore.toPlainString());
    }


    public void addFinishRows(int rows) {
        finishRows += rows;
        level = Math.floorDiv(finishRows, LEVELUP) + 1;
    }

    public void incrementLevel() {
        level++;
    }

    public void resetScores() {
        mainscore = new BigDecimal(0);
        currentSpeedRate = 1.0;
        finishRows = 0;
        level = 1;
    }
}
