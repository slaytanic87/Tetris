package de.tetris.controller.interfaces;

/**
 * Abstract controller for other input interfaces.
 * @author Lam
 */
public interface IGameController {

    void moveLeft();

    void moveRight();

    void moveDown();

    void moveUp();

    void rotateLeft();

    void rotateRight();

    void toggleModelView();

    void startGame();

    void stopGame();

    void pause();
}
