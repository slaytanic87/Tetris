package de.tetris.utils;

import de.tetris.model.block.Block;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.eventbus.EventBus;

import java.util.List;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class MessageEventUtils {

    private static final Object mutex = new Object();
    public static String WEBSOCKET_EVENT_FIELD_DATA = "event.websocket.fielddata";
    public static String WEBSOCKET_EVENT_CURRENT_BLOCK = "event.websocket.block";

    private static volatile MessageEventUtils INSTANCE = null;
    private EventBus eventBus;

    public static MessageEventUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (mutex) {       // While we were waiting for the mutex, another
                if (INSTANCE == null) {  // thread may have instantiated the object.
                    INSTANCE = new MessageEventUtils();
                }
            }
        }
        return INSTANCE;
    }

    public void sendDataToWebSocketBus(List<List<String>> field) {
        eventBus.publish(WEBSOCKET_EVENT_FIELD_DATA, Json.encode(field));
    }

    public void sendBlockToWebSocketBus(Block block) {
        eventBus.publish(WEBSOCKET_EVENT_CURRENT_BLOCK, Json.encode(block));
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}