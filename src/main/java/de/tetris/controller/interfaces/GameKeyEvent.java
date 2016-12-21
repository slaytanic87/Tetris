package de.tetris.controller.interfaces;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lam
 */
@Slf4j
public class GameKeyEvent implements EventHandler<KeyEvent> {

    private IGameController controller;

    public GameKeyEvent(IGameController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(KeyEvent key) {
        log.debug("{}", key.getCode());

        if (key.getEventType() == KeyEvent.KEY_RELEASED) {
            switch (key.getCode()) {
                case CONTROL:
                    controller.rotateLeft();
                    break;
                case ALT:
                    controller.rotateRight();
                    break;
                case G:
                    controller.toggleModelView();
                    break;
                case SPACE:
                    controller.startGame();
                    break;
                default:
                    break;
            }
        } else if (key.getEventType() == KeyEvent.KEY_PRESSED) {
            switch (key.getCode()) {
                case DOWN:
                    controller.moveDown();
                    break;
                case UP:
                    controller.moveUp();
                    break;
                case LEFT:
                    controller.moveLeft();
                    break;
                case RIGHT:
                    controller.moveRight();
                    break;
                case P:
                    controller.pause();
                    break;
                case ESCAPE:
                    controller.stopGame();
                    break;
                default: break;
            }
        }
    }
}
