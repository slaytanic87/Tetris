package de.tetris.model;

import lombok.Getter;

/**
 * @author Lam
 */
@Getter
public class GameState {

    private boolean isDebug = false;
    private boolean isPaused = false;
    private boolean isFinished = true;

    public GameState() {
    }

    public void setIsFinished() {
        this.isFinished = true;
    }

    public void setIsNotFinished() {
        this.isFinished = false;
    }

    public void setIsPaused() {
        this.isPaused = true;
    }

    public void setIsNotPaused() {
        this.isPaused = false;
    }

    public void debugOn() {
        this.isDebug = true;
    }

    public void debugOff() {
        this.isDebug = false;
    }
}
