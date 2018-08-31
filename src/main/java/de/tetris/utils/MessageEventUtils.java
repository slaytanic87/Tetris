package de.tetris.utils;

import io.vertx.core.json.Json;
import io.vertx.rxjava.core.eventbus.EventBus;

import java.util.List;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class MessageEventUtils {

    private static final Object mutex = new Object();
    public static String WEBSOCKET_EVENT_FIELD_DATA = "event.websocket.fielddata";
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

    public void sendDataToWebSocket(List<List<String>> field) {
        eventBus.publish(WEBSOCKET_EVENT_FIELD_DATA, Json.encode(field));
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}