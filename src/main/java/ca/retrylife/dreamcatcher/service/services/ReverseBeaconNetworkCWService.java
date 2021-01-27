package ca.retrylife.dreamcatcher.service.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.retrylife.dreamcatcher.event.EventBase;
import ca.retrylife.dreamcatcher.event.events.RBNEvent;

public class ReverseBeaconNetworkCWService extends ReverseBeaconNetworkServiceBase {

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Pattern matcher for RBN information
    private static final Pattern INFO_MATCH_PATTERN = Pattern.compile(
            "DX de ([A-Z\\d\\/-]+)-#:\\s*([\\d.]+)\\s+([A-Z\\d\\/-]+)\\s+([A-Z\\d]+)\\s+([\\d-]+) dB\\s+(\\d+) [WPMBPS]+\\s+([A-Za-z\\d ]+)\\s*([0-9]{4})Z",
            Pattern.MULTILINE);

    public ReverseBeaconNetworkCWService() {
        super(7000);
    }

    @Override
    protected EventBase handleTelnetMessage(String message) {

        // Find all data
        Matcher matcher = INFO_MATCH_PATTERN.matcher(message);

        // Skip if no data
        if (!matcher.find()) {
            logger.debug("Failed to parse a line from RBN");
            return null;
        }

        // Put together data
        RBNEvent event = new RBNEvent();
        event.setSpotter(matcher.group(1));
        event.setFrequency(Double.parseDouble(matcher.group(2).replace("\\s*", "")));
        event.setSpotted(matcher.group(3).replace("\\s*", ""));
        event.setMode(matcher.group(4).replace("\\s*", ""));
        event.setTimestamp(System.currentTimeMillis());
        event.getData().put("snr", matcher.group(5).replace("\\s*", ""));
        event.getData().put("speed", matcher.group(6).replace("\\s*", ""));
        event.getData().put("message", matcher.group(7).replace(" ", ""));

        return event;

    }

    @Override
    public String getServiceName() {
        return getClass().getName();
    }

}