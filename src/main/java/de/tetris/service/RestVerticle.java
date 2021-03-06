package de.tetris.service;

import de.tetris.model.rest.Command;
import de.tetris.model.rest.ErrorCode;
import de.tetris.model.rest.Response;
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
    private static int port = 3000;
    public static final int UNKNOWN_COMMAND_CODE = 501;
    public static final int NO_SUCCESS_EVENT_CODE = 500;

    public RestVerticle(Integer port) {
        if (port != null && port > 0) {
            this.port = port;
        }
    }

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

        apiRouter.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Access-Control-Request-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Content-Type"));
        // enables the reading of the request body for all routes globally
        apiRouter.route().handler(BodyHandler.create());

        apiRouter.get("/field").handler(this::handleField);
        apiRouter.get("/gamestate").handler(this::handleGamestate);
        apiRouter.get("/speed").handler(this::handleGameSpeed);
        apiRouter.get("/level").handler(this::handleGameLevel);
        apiRouter.get("/finishedrows").handler(this::handleProcessedRows);
        apiRouter.get("/waitingblocks").handler(this::handleQueueBlock);
        apiRouter.post("/turn/").handler(this::handleGameController);
        apiRouter.get("/bricklevel").handler(this::handleBrickLevel);
        apiRouter.get("/blockdisplacement").handler(this::handleBlockDisplacement);

        final HttpServerOptions options = new HttpServerOptions().setCompressionSupported(true);

        // create http server.
        vertx.createHttpServer(options).requestHandler(router::accept)
                // Integer.parseInt(config().getString("port"))
                .listen(port,
                        event -> {
                            if (event.succeeded()) {
                                log.info("Server started at port {}", port);
                                startFuture.complete();
                            } else {
                                startFuture.fail(event.cause());
                            }
                        });
    }

    private void handleBlockDisplacement(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_BLOCK_DISPLACEMENT, null,
                createResponseHandler(routingContext));
    }

    private void handleBrickLevel(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_BRICK_LEVEL, null,
                createResponseHandler(routingContext));
    }

    private void handleQueueBlock(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_BLOCK_QUEUE, null,
                createResponseHandler(routingContext));
    }

    private void handleProcessedRows(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_FINISHED_ROWS, null,
                createResponseHandler(routingContext));
    }

    private void handleGameLevel(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_GAME_LEVEL, null,
                createResponseHandler(routingContext));
    }

    private void handleGameSpeed(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_GAME_SPEED, null,
                createResponseHandler(routingContext));
    }

    private void handleGamestate(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_GAME_PROGRESS_STATE, null,
                createResponseHandler(routingContext));
    }

    private void handleField(RoutingContext routingContext) {
        vertx.eventBus().send(DataModelVerticle.EVENT_GET_FIELD, null, createResponseHandler(routingContext));
    }


    private void handleGameController(RoutingContext routingContext) {
        Command cmd = Json.decodeValue(routingContext.getBodyAsString(), Command.class);

        switch (cmd.getCmd()) {
            case 1:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_LEFT, null,
                        createResponseHandler(routingContext));
                break;
            case 2:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_RIGHT, null,
                        createResponseHandler(routingContext));
                break;
            case 3:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_DOWN, null,
                        createResponseHandler(routingContext));
                break;
            case 4:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_ROTATE_LEFT, null,
                        createResponseHandler(routingContext));
                break;
            case 5:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_ROTATE_RIGHT, null,
                        createResponseHandler(routingContext));
                break;
            case 6:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_START, null,
                        createResponseHandler(routingContext));
                break;
            case 7:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_PAUSE, null,
                        createResponseHandler(routingContext));
                break;
            case 8:
                vertx.eventBus().send(GameInputBusEventVerticle.EVENT_STOP, null,
                        createResponseHandler(routingContext));
                break;
            default:
                Response response = new Response();
                response.setCode(ErrorCode.UNKNOWN);
                response.setMessage("UNKNOWN_COMMAND");
                routingContext.response().putHeader("content-type", APPLICATION_JSON)
                        .setStatusCode(UNKNOWN_COMMAND_CODE)
                        .end(Json.encode(response));
        }
    }

    private Handler<AsyncResult<Message<String>>> createResponseHandler(RoutingContext routingContext) {
        return event -> {
            if (event.succeeded()) {
                routingContext.response().putHeader("content-type", APPLICATION_JSON)
                        .end(event.result().body());
            } else {
                routingContext.response().putHeader("content-type", TEXT_PLAIN)
                        .setStatusCode(NO_SUCCESS_EVENT_CODE)
                        .end(event.cause().getMessage());
            }
        };
    }

}