package ca.retrylife.dreamcatcher.service.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.retrylife.dreamcatcher.AuthManager;
import ca.retrylife.dreamcatcher.event.EventBase;
import ca.retrylife.dreamcatcher.server.MessageSender;
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
        logger.info(String.format("Opening connection to %s:%d", RBN_TELNET_ENDPOINT, this.remotePort));
        try (Socket socket = new Socket(RBN_TELNET_ENDPOINT, this.remotePort)) {

            // Get in and out streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            // Consume the login message
            logger.debug("Consuming login prompt");
            int chr;
            char lastChr = ' ';
            while (((chr = in.read()) != -1) && lastChr != ':') {
                lastChr = (char) chr;
            }

            // Write the login information
            logger.info("Logging in to RBN");
            out.write(AuthManager.getInstance().getReverseBeaconNetworkKey() + "\r\n\r\n");
            out.flush();
            logger.debug("Logged in");

            // Pass along every new bit of information to the subclass
            StringBuilder messageBuilder = new StringBuilder();
            int nextChr;
            while (true) {

                // Consume a line
                while ((nextChr = in.read()) != -1) {
                    if (((char) nextChr) == '\r') {
                        in.read();
                        break;
                    } else {
                        messageBuilder.append((char) nextChr);
                    }
                }

                // Handle the message
                String message = messageBuilder.toString();
                logger.debug(message);
                EventBase event = this.handleTelnetMessage(message);

                // Send the event
                if (event != null) {
                    MessageSender.getInstance().sendEvent(event);
                }

                // Clear the buffer
                messageBuilder = new StringBuilder();
            }

        } catch (IOException e) {
            logger.error(String.format("Failed to open connection to %s:%d", RBN_TELNET_ENDPOINT, this.remotePort));
            return;
        }

    }

    protected abstract EventBase handleTelnetMessage(String message);

    @Override
    public void launch() {
        clientThread.start();
    }

    @Override
    public void kill() {
        clientThread.stop();
    }

}