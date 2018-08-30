package de.tetris.service;

import de.tetris.controller.interfaces.DataModelVerticle;
import de.tetris.controller.interfaces.GameInputBusEventVerticle;
import de.tetris.model.rest.Command;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Lam
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RestVerticle extends AbstractVerticle {

    private static final int INIT_PARSER_SIZE = 1;
    private static final String APPLICATION_JSON = "application/json";
    private static final String TEXT_PLAIN = "text/plain";
    private static final int PORT = 3000;

    @Override
    public void start(Future<Void> startFuture) {
        final Router router = Router.router(vertx);

        final Router apiRouter = Router.router(vertx);
        router.mountSubRouter("/api", apiRouter);

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions bridgeOptions = new BridgeOptions();
        bridgeOptions.addOutboundPermitted(new PermittedOptions()
                .setAddressRegex(".*"));
        sockJSHandler.bridge(bridgeOptions);
        // Set websocket event bus
        apiRouter.route("/eventbus/*").handler(sockJSHandler);

        // enables the reading of the request body for all routes globally
        apiRouter.route().handler(BodyHandler.create());
        apiRouter.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Access-Control-Request-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Content-Type"));

        apiRouter.get("/field").handler(this::handleField);
        apiRouter.get("/gamestate").handler(this::handleGamestate);
        apiRouter.get("/speed").handler(this::handleGameSpeed);
        apiRouter.get("/level").handler(this::handleGameLevel);
        apiRouter.get("/finishedrows").handler(this::handleProcessedRows);
        apiRouter.post("/turn/").handler(this::handleGameController);

        final HttpServerOptions options = new HttpServerOptions().setCompressionSupported(true);

        // create http server.
        vertx.createHttpServer(options).requestHandler(router::accept)
                // Integer.parseInt(config().getString("port"))
                .listen(PORT,
                        event -> {
                            if (event.succeeded()) {
                                log.info("Server started at port {}", PORT);
                                startFuture.complete();
                            } else {
                                startFuture.fail(event.cause());
                            }
                        });
    }

    private void handleProcessedRows(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_FINISHED_ROWS, null,
                getDefaultStringHandler(routingContext));
    }

    private void handleGameLevel(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_GAME_LEVEL, null,
                getDefaultStringHandler(routingContext));
    }

    private void handleGameSpeed(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_GAME_SPEED, null,
                getDefaultStringHandler(routingContext));
    }

    private void handleGamestate(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_GAME_PROGRESS_STATE, null,
                getDefaultStringHandler(routingContext));
    }

    private void handleField(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_FIELD, null, getDefaultStringHandler(routingContext));
    }


    private void handleGameController(RoutingContext routingContext) {
        Command cmd = Json.decodeValue(routingContext.getBodyAsString(), Command.class);

        switch (cmd.getCmd()) {
            case 1:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_LEFT, null,
                        getDefaultStringHandler(routingContext));
                break;
            case 2:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_RIGHT, null,
                        getDefaultStringHandler(routingContext));
                break;
            case 3:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_DOWN, null,
                        getDefaultStringHandler(routingContext));
                break;
            case 4:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_ROTATE_LEFT, null,
                        getDefaultStringHandler(routingContext));
                break;
            case 5:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_ROTATE_RIGHT, null,
                        getDefaultStringHandler(routingContext));
                break;
            case 6:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_START, null,
                        getDefaultStringHandler(routingContext));
                break;
            case 7:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_PAUSE, null,
                        getDefaultStringHandler(routingContext));
                break;
            case 8:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_STOP, null,
                        getDefaultStringHandler(routingContext));
                break;
            default:
                routingContext.response().putHeader("content-type", TEXT_PLAIN)
                        .setStatusCode(501)
                        .putHeader("content-type", "text/plain")
                        .end("UNKNOWN_COMMAND");
        }
    }

    private Handler<AsyncResult<Message<String>>> getDefaultStringHandler(RoutingContext routingContext) {
        return event -> {
            if (event.succeeded()) {
                routingContext.response().putHeader("content-type", APPLICATION_JSON)
                        .end(event.result().body());
            } else {
                routingContext.response().putHeader("content-type", TEXT_PLAIN)
                        .setStatusCode(500)
                        .end(event.cause().getMessage());
            }
        };
    }

}