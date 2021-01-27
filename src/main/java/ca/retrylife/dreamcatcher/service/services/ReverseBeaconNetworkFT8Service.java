package ca.retrylife.dreamcatcher.service.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.retrylife.dreamcatcher.event.EventBase;
import ca.retrylife.dreamcatcher.event.events.RBNEvent;

public class ReverseBeaconNetworkFT8Service extends ReverseBeaconNetworkServiceBase {

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Pattern matcher for RBN information
    private static final Pattern INFO_MATCH_PATTERN = Pattern.compile(
            "DX de ([A-Z\\d-]+)-#:\\s*([\\d.]+)\\s+([A-Z\\d-]+)\\s+([A-Z\\d]+)\\s+([\\d-]+) dB\\s+([A-Z\\d]{4}){0,4}",
            Pattern.MULTILINE);

    public ReverseBeaconNetworkFT8Service() {
        super(7001);
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
        if (matcher.group(6) != null) {
            event.getData().put("grid", matcher.group(6).replace("\\s*", ""));
        } else {
            event.getData().put("grid", null);
        }

        return event;

    }

    @Override
    public String getServiceName() {
        return getClass().getName();
    }

}