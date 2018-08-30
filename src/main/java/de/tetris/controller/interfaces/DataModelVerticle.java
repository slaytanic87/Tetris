package de.tetris.controller.interfaces;

import de.tetris.model.GameState;
import de.tetris.model.Scores;
import de.tetris.model.TetrisField;
import de.tetris.model.rest.ErrorCode;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Slf4j
public class DataModelVerticle extends AbstractVerticle {

    public static final String EVENT_GET_FIELD = "event.field";
    public static final String EVENT_GET_GAME_PROGRESS_STATE = "event.gameState";
    public static final String EVENT_GET_GAME_SPEED = "event.gameSpeed";
    public static final String EVENT_GET_GAME_LEVEL = "event.gameLevel";
    public static final String EVENT_GET_FINISHED_ROWS = "event.finishedRows";

    private TetrisField model;

    public DataModelVerticle(TetrisField model) {
        this.model = model;
    }

    @Override
    public void start() {
        vertx.eventBus().consumer(EVENT_GET_FIELD, this::handleGetField);
        vertx.eventBus().consumer(EVENT_GET_GAME_PROGRESS_STATE, this::handleGetProgressState);
        vertx.eventBus().consumer(EVENT_GET_GAME_SPEED, this::handleGameSpeed);
        vertx.eventBus().consumer(EVENT_GET_GAME_LEVEL, this::handleGameLevel);
        vertx.eventBus().consumer(EVENT_GET_FINISHED_ROWS, this::handleFinishedRows);
    }

    private void handleFinishedRows(Message<String> message) {
        JsonObject json = new JsonObject().put("finished", Scores.getInstance().getFinishRows());
        message.reply(json.encode());
    }

    private void handleGameLevel(Message<String> message) {
        JsonObject json = new JsonObject().put("level", Scores.getInstance().getLevel());
        message.reply(json.encode());
    }

    private void handleGameSpeed(Message<String> message) {
        JsonObject json = new JsonObject().put("speedRate", Scores.getInstance().getCurrentSpeedRate());
        message.reply(json.encode());
    }

    private void handleGetField(Message<String> message) {
        try {
            message.reply(Json.encode(model.getField()));
        } catch (EncodeException e) {
            log.debug("Error {}", e.getMessage());
            message.fail(ErrorCode.INTERNAL_ERROR.getCode(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void handleGetProgressState(Message<String> message) {
        message.reply(Json.encode(GameState.getInstance()));
    }
}