package ca.retrylife.dreamcatcher.server;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPServer {

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Internal instance reference
    private static HTTPServer instance = null;

    /**
     * Get the global instance of HTTPServer
     *
     * @return HTTPServer instance
     */
    public static HTTPServer getInstance() {
        if (instance == null) {
            instance = new HTTPServer();
        }
        return instance;
    }

    // All connected clients
    private List<WsContext> connectedClients = new ArrayList<>();

    // Hidden constructor to force singleton usage
    private HTTPServer() {

        Javalin.create(config -> {
            config.addStaticFiles("/html");
        }).ws("/waterfall", ws -> {
            ws.onConnect(ctx -> {
                logger.info(String.format("New session: %s", ctx.getSessionId()));
                connectedClients.add(ctx);
            });
            ws.onClose(ctx -> {
                logger.info(String.format("Session disconnect: %s", ctx.getSessionId()));
                connectedClients.remove(ctx);
            });
        }).start(8887);
    }

    public void send(String message) {
        this.connectedClients.forEach((client) -> {
            client.send(message);
        });
    }

}