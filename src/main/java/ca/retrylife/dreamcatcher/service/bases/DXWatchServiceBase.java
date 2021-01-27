package ca.retrylife.dreamcatcher.service.bases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.retrylife.dreamcatcher.AuthManager;
import ca.retrylife.dreamcatcher.event.EventBase;
import ca.retrylife.dreamcatcher.event.events.DXWatchEvent;
import ca.retrylife.dreamcatcher.server.MessageSender;
import ca.retrylife.dreamcatcher.service.Service;

/**
 * Client base for all data provided by DXWatch
 */
public class DXWatchServiceBase implements Service {

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Internal thread used for handling Telnet connection
    private Thread clientThread;

    // Network connection info
    private final int remotePort;
    private final String address;

    // Pattern matcher for DXWatch information
    private static final Pattern INFO_MATCH_PATTERN = Pattern.compile(
            "DX de ([A-Z\\d\\/-]+):\\s+([\\d.]+)\\s*([A-Z\\d\\/-]+)\\s+(.*)\\s+(\\d+)Z\\s*([A-Z\\d]{4}){0,4}",
            Pattern.MULTILINE);

    public DXWatchServiceBase(String address, int remotePort) {
        this.remotePort = remotePort;
        this.address = address;

        // Set up threads
        clientThread = new Thread(this::execute);
    }

    private void execute() {

        // Open a connection
        logger.info(String.format("Opening connection to %s:%d", this.address, this.remotePort));
        try (Socket socket = new Socket(this.address, this.remotePort)) {

            // Get in and out streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            // Consume the login message
            logger.debug("Consuming login prompt");
            int chr;
            char lastChr = ' ';
            while (((chr = in.read()) != -1) && !(chr == ':' && lastChr == 'n')) {
                lastChr = (char) chr;
            }
            in.read();

            // Write the login information
            logger.info("Logging in to " + this.address);
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
            logger.error(String.format("Failed to open connection to %s:%d", this.address, this.remotePort));
            return;
        }

    }

    private EventBase handleTelnetMessage(String message) {

        // Find all data
        Matcher matcher = INFO_MATCH_PATTERN.matcher(message);

        // Skip if no data
        if (!matcher.find()) {
            logger.debug("Failed to parse a line from DXWatch");
            return null;
        }

        // Put together data
        DXWatchEvent event = new DXWatchEvent();
        event.setSpotter(matcher.group(1));
        event.setFrequency(Double.parseDouble(matcher.group(2).replace("\\s*", "")));
        event.setSpotted(matcher.group(3).replace("\\s*", ""));
        event.setTimestamp(System.currentTimeMillis());
        if (matcher.group(4) != null) {
            event.setMessage(matcher.group(4));
        }
        if (matcher.group(6) != null) {
            event.setGrid(matcher.group(6).replace(" ", ""));
        }

        return event;
    }

    @Override
    public void launch() {
        clientThread.start();
    }

    @Override
    public void kill() {
        clientThread.stop();
    }

    @Override
    public String getServiceName() {
        return getClass().getName();
    }

}