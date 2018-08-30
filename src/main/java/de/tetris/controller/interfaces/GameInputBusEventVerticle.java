package de.tetris.controller.interfaces;

import de.tetris.model.GameState;
import de.tetris.model.rest.ErrorCode;
import de.tetris.model.rest.Response;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lam
 */
@Slf4j
public class GameInputBusEventVerticle extends AbstractVerticle {

    public static final String EVENT_STOP = "event.stop";
    public static final String EVENT_PAUSE = "event.pause";
    public static final String EVENT_START = "event.start";
    public static final String EVENT_DOWN = "event.down";
    public static final String EVENT_LEFT = "event.left";
    public static final String EVENT_RIGHT = "event.right";
    public static final String EVENT_ROTATE_LEFT = "event.rotateleft";
    public static final String EVENT_ROTATE_RIGHT = "event.rotateright";

    private IGameController controller;

    public GameInputBusEventVerticle(IGameController controller) {
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
        vertx.eventBus().consumer(EVENT_PAUSE, this::handlePause);
        vertx.eventBus().consumer(EVENT_STOP, this::handleStop);
    }

    @Override
    public void stop() {
    }

    private void handleDown(Message<String> message) {
        log.debug("Event: {}", "MOVE DOWN");
        Response response = new Response();
        if (!GameState.getInstance().isStopped() && !GameState.getInstance().isPaused()) {
            Platform.runLater(() -> controller.moveDown());
            response.setCode(ErrorCode.OK);
            response.setMessage("Ok");
        } else {
            response.setCode(ErrorCode.NOK);
            response.setMessage("Game not running");
        }
        message.reply(Json.encode(response));
    }

    private void handleLeft(Message<String> message) {
        log.debug("Event: {}", "MOVE LEFT");
        Response response = new Response();
        if (!GameState.getInstance().isStopped() && !GameState.getInstance().isPaused()) {
            Platform.runLater(() -> controller.moveLeft());
            response.setCode(ErrorCode.OK);
            response.setMessage("Ok");
        } else {
            response.setCode(ErrorCode.NOK);
            response.setMessage("Game not running");
        }
        message.reply(Json.encode(response));
    }

    private void handleRight(Message<String> message) {
        log.debug("Event: {}", "MOVE RIGHT");
        Response response = new Response();
        if (!GameState.getInstance().isStopped() && !GameState.getInstance().isPaused()) {
            Platform.runLater(() -> controller.moveRight());
            response.setCode(ErrorCode.OK);
            response.setMessage("Ok");
        } else {
            response.setCode(ErrorCode.NOK);
            response.setMessage("Game not running");
        }
        message.reply(Json.encode(response));
    }

    private void handleRotateLeft(Message<String> message) {
        log.debug("Event: {}", "ROTATE LEFT");
        Response response = new Response();
        if (!GameState.getInstance().isStopped() && !GameState.getInstance().isPaused()) {
            Platform.runLater(() -> controller.rotateLeft());
            response.setCode(ErrorCode.OK);
            response.setMessage("Ok");
        } else {
            response.setCode(ErrorCode.NOK);
            response.setMessage("Game not running");
        }
        message.reply(Json.encode(response));
    }

    private void handleRotateRight(Message<String> message) {
        log.debug("Event: {}", "ROTATE RIGHT");
        Response response = new Response();
        if (!GameState.getInstance().isStopped() && !GameState.getInstance().isPaused()) {
            Platform.runLater(() -> controller.rotateRight());
            response.setCode(ErrorCode.OK);
            response.setMessage("Ok");
        } else {
            response.setCode(ErrorCode.NOK);
            response.setMessage("Game not running");
        }
        message.reply(Json.encode(response));
    }

    private void handleStart(Message<String> message) {
        log.debug("Event: {}", "Start");
        Response response = new Response();
        if (!GameState.getInstance().isStopped() && !GameState.getInstance().isPaused()) {
            Platform.runLater(() -> controller.startGame());
            response.setCode(ErrorCode.OK);
            response.setMessage("Ok");
        } else {
            response.setCode(ErrorCode.NOK);
            response.setMessage("Game not running");
        }
        message.reply(Json.encode(response));
    }

    private void handlePause(Message<String> message) {
        log.debug("Event: {}", "Pause");
        Response response = new Response();
        if (!GameState.getInstance().isStopped() && !GameState.getInstance().isPaused()) {
            Platform.runLater(() -> controller.pause());
            response.setCode(ErrorCode.OK);
            response.setMessage("Ok");
        } else {
            response.setCode(ErrorCode.NOK);
            response.setMessage("Game not running");
        }
        message.reply(Json.encode(response));
    }

    private void handleStop(Message<String> message) {
        log.debug("Event: {}", "Stop");
        Response response = new Response();
        if (!GameState.getInstance().isStopped() && !GameState.getInstance().isPaused()) {
            Platform.runLater(() -> controller.stopGame());
            response.setCode(ErrorCode.OK);
            response.setMessage("Ok");
        } else {
            response.setCode(ErrorCode.NOK);
            response.setMessage("Game not running");
        }
        message.reply(Json.encode(response));
    }
}