package ca.retrylife.dreamcatcher.service.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.retrylife.dreamcatcher.AuthManager;
import ca.retrylife.dreamcatcher.service.Service;

/**
 * Client base for all data provided by the reverse beacon network
 */
public abstract class ReverseBeaconNetworkServiceBase implements Service {

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // RBN endpoint
    private static final String RBN_TELNET_ENDPOINT = "telnet.reversebeacon.net";

    // Internal thread used for handling Telnet connection
    private Thread clientThread;

    // Network connection info
    private final int remotePort;

    protected ReverseBeaconNetworkServiceBase(int remotePort) {
        this.remotePort = remotePort;

        // Set up threads
        clientThread = new Thread(this::execute);
    }

    private void execute() {

        // Open a connection
        try (Socket socket = new Socket(RBN_TELNET_ENDPOINT, this.remotePort)) {

            // Get in and out streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            // Consume the login message
            while (in.readLine() != null) {
            }

            // Write the login information
            out.println(AuthManager.getInstance().getReverseBeaconNetworkKey());

            // Pass along every new bit of information to the subclass
            String line;
            while ((line = in.readLine()) != null) {
                this.handleTelnetMessage(line);
            }

        } catch (IOException e) {
            logger.error("Failed to open connection to %s:%d", RBN_TELNET_ENDPOINT, this.remotePort);
            return;
        }

    }

    protected abstract void handleTelnetMessage(String message);

    @Override
    public void launch() {
        clientThread.start();
    }

    @Override
    public void kill() {
        clientThread.stop();
    }

}