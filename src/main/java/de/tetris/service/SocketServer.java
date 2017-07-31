package de.tetris.service;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.net.NetSocket;
import io.vertx.rxjava.core.parsetools.RecordParser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lam
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class SocketServer extends AbstractVerticle {

    private static final int port = 1000;
    private static final int INIT_PARSER_SIZE = 1;

    @Override
    public void start(Future<Void> startFuture) {
        NetServerOptions options = new NetServerOptions();
        vertx.createNetServer(options).connectHandler(socket -> {
            // TODO handle incoming connection
            processContent(vertx, socket);
        }).listen(port, event -> {
            if (event.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(new RuntimeException("unable to bind port: " + port));
            }
        });
    }

    private void processContent(Vertx vertx, NetSocket netSocket) {
        final RecordParser parser = RecordParser.newFixed(INIT_PARSER_SIZE, null);

        parser.setOutput(buff -> {
            byte[] incomingBytes = ((Buffer) buff.getDelegate()).getBytes();
            // TODO read header, content etc.
        });

        netSocket.handler(parser);
    }

}
