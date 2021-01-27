package ca.retrylife.dreamcatcher.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSServer extends WebSocketServer {

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Internal instance reference
    private static WSServer instance = null;

    /**
     * Get the global instance of WSServer
     *
     * @return WSServer instance
     */
    public static WSServer getInstance() {
        if (instance == null) {
            instance = new WSServer();
        }
        return instance;
    }

    // Hidden constructor to force singleton usage
    private WSServer() {
        super(new InetSocketAddress(8887));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        // Get user info
        String descriptor = conn.getResourceDescriptor();
        String hostAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress();

        logger.info(String.format("%s(%s) Connected", hostAddress, descriptor));

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStart() {
        logger.info("WebSocket Server Started");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);

    }

    public void send(String message) {
        broadcast(message);
    }

}