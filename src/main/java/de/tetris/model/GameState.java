package de.tetris.model;

import de.tetris.model.block.Block;
import lombok.Getter;

/**
 * @author Lam
 */
@Getter
public class GameState {

    private static final Object mutex = new Object();

    private static volatile GameState INSTANCE = null;
    private Block suggestedBlockWithPosition;

    private boolean isDebug = false;
    private boolean isPaused = false;
    private boolean isFinished = true;
    private boolean isStopped = true;

    private GameState() {
    }

    public static GameState getInstance() {
        if (INSTANCE == null) {
            synchronized (mutex) {       // While we were waiting for the mutex, another
                if (INSTANCE == null) {  // thread may have instantiated the object.
                    INSTANCE = new GameState();
                }
            }
        }
        return INSTANCE;
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

    public void setIsStopped() {
        this.isStopped = true;
    }

    public void setIsNotStopped() {
        this.isStopped = false;
    }

    public void debugOn() {
        this.isDebug = true;
    }

    public void debugOff() {
        this.isDebug = false;
    }

    public void setSuggestedBlockWithPosition(Block blockWithPosition) {
        this.suggestedBlockWithPosition = blockWithPosition;
    }
}
