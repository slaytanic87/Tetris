package de.tetris.controller.interfaces;

import de.tetris.model.GameState;
import de.tetris.model.TetrisField;
import de.tetris.model.rest.ErrorCode;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Slf4j
public class DataModelVerticle extends AbstractVerticle {

    public static final String EVENT_GET_FIELD = "event.field";
    public static final String EVENT_GET_GAME_PROGRESS_STATE = "event.gamestate";

    private TetrisField model;

    public DataModelVerticle(TetrisField model) {
        this.model = model;
    }

    @Override
    public void start() {
        vertx.eventBus().consumer(EVENT_GET_FIELD, this::handleGetField);
        vertx.eventBus().consumer(EVENT_GET_GAME_PROGRESS_STATE, this::handleGetProgressState);
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