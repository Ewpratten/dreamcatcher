package ca.retrylife.dreamcatcher.server;

import ca.retrylife.dreamcatcher.event.EventBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSender {

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Internal instance reference
    private static MessageSender instance = null;

    /**
     * Get the global instance of MessageSender
     *
     * @return MessageSender instance
     */
    public static MessageSender getInstance() {
        if (instance == null) {
            instance = new MessageSender();
        }
        return instance;
    }

    // Hidden constructor to force singleton usage
    private MessageSender() {
    }

    public void sendEvent(EventBase event) {
        // Get JSON data
        String json = event.toJson();

        WSServer.getInstance().send(json);
    }

}