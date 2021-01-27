package ca.retrylife.dreamcatcher.service.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.retrylife.dreamcatcher.AuthManager;
import ca.retrylife.dreamcatcher.event.EventBase;
import ca.retrylife.dreamcatcher.event.events.APRSEvent;
import ca.retrylife.dreamcatcher.server.MessageSender;
import ca.retrylife.dreamcatcher.service.Service;
import ca.retrylife.dreamcatcher.utils.APRSUtils;
import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.Parser;

public class APRSFIService implements Service {

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Internal thread used for handling Telnet connection
    private Thread clientThread;

    // Network connection info
    private static final String APRS_IS_HOST = "first.aprs.net";
    private static final int APRS_IS_PORT = 10152;

    public APRSFIService() {

        // Set up threads
        clientThread = new Thread(this::execute);
    }

    private void execute() {

        try {
            // Open a connection
            TelnetClient client = new TelnetClient();
            logger.info(String.format("Opening connection to %s:%d", APRS_IS_HOST, APRS_IS_PORT));
            client.setConnectTimeout(3000);
            client.connect(APRS_IS_HOST, APRS_IS_PORT);

            // Get in and out streams
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());

            // Consume the login
            logger.debug("Consuming login prompt");
            in.readLine();

            // Write login information
            logger.info("Logging in");
            out.write(AuthManager.getInstance().getAPRSKey() + "\r\n\r\n");
            out.flush();
            logger.debug("Logged in");

            // Read every new line sent
            String message;
            while ((message = in.readLine()) != null) {
                // Handle the message
                logger.debug(message);
                EventBase event = this.handleTelnetMessage(message);

                // Send the event
                if (event != null) {
                    MessageSender.getInstance().sendEvent(event);
                }
            }

        } catch (IOException e) {
            logger.error(String.format("Failed to open connection to %s:%d", APRS_IS_HOST, APRS_IS_PORT));
            return;
        }

    }

    private EventBase handleTelnetMessage(String message) {

        // Parse the aprs packet
        APRSPacket packet = null;
        try {
            packet = Parser.parse(message);
        } catch (Exception e) {
            logger.debug("Failed to parse a line from APRS");
            return null;
        }

        // Put together data
        APRSEvent event = new APRSEvent();
        event.setSource(packet.getSourceCall());
        event.setDestination(packet.getDestinationCall());
        event.setRaw(packet.getOriginalString());
        event.setDti(packet.getDti());
        event.setTimestamp(System.currentTimeMillis());
        event.setMessage_type(APRSUtils.toTypeString(packet.getType()));
        event.setDigipeaters(APRSUtils.digipeatersToCallsigns(packet.getDigipeaters()));
        event.setComment(packet.getAprsInformation().getComment());

        // Handle information
        if (packet.getAprsInformation().getExtension() != null) {
            event.setMessage(packet.getAprsInformation().getExtension().toSAEString());
        }

        // return event;
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