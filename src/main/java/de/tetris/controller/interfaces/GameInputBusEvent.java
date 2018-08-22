package de.tetris.controller.interfaces;

import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lam
 */
@Slf4j
public class GameInputBusEvent extends AbstractVerticle {

    public static final String EVENT_START = "event.start";
    public static final String EVENT_DOWN = "event.down";
    public static final String EVENT_LEFT = "event.left";
    public static final String EVENT_RIGHT = "event.right";
    public static final String EVENT_ROTATE_LEFT = "event.rotateleft";
    public static final String EVENT_ROTATE_RIGHT = "event.rotateright";

    private IGameController controller;

    public GameInputBusEvent(IGameController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        vertx.eventBus().consumer(EVENT_DOWN, this::handleDown);
        vertx.eventBus().consumer(EVENT_LEFT, this::handleLeft);
        vertx.eventBus().consumer(EVENT_RIGHT, this::handleRight);
        vertx.eventBus().consumer(EVENT_ROTATE_LEFT, this::handleRotateLeft);
        vertx.eventBus().consumer(EVENT_ROTATE_RIGHT, this::handleRotateRight);
        vertx.eventBus().consumer(EVENT_START, this::handleStart);
    }

    private void handleDown(Message<String> message) {
        log.debug("Event: {}", "MOVE DOWN");
        Platform.runLater(() -> controller.moveDown());
        message.reply("Ok");
    }

    private void handleLeft(Message<String> message) {
        log.debug("Event: {}", "MOVE LEFT");
        Platform.runLater(() -> controller.moveLeft());
        message.reply("Ok");
    }

    private void handleRight(Message<String> message) {
        log.debug("Event: {}", "MOVE RIGHT");
        Platform.runLater(() -> controller.moveRight());
        message.reply("Ok");
    }

    private void handleRotateLeft(Message<String> message) {
        log.debug("Event: {}", "ROTATE LEFT");
        Platform.runLater(() -> controller.rotateLeft());
        message.reply("Ok");
    }

    private void handleRotateRight(Message<String> message) {
        log.debug("Event: {}", "ROTATE RIGHT");
        Platform.runLater(() -> controller.rotateRight());
        message.reply("Ok");
    }

    private void handleStart(Message<String> message) {
        log.debug("Event: {}", "Start");
        Platform.runLater(() -> controller.startGame());
        message.reply("Ok");
    }

}