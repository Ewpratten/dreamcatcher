package ca.retrylife.dreamcatcher.event.events;

import com.google.gson.Gson;

import ca.retrylife.dreamcatcher.event.EventBase;
import lombok.Data;

@Data
public class APRSEvent extends EventBase {

    String raw;
    String source;
    String destination;
    String[] digipeaters;
    char dti;
    String message_type;
    String message = null;
    String comment;

    public APRSEvent() {
        super.setType("aprs_packet");
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }

}