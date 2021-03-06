package ca.retrylife.dreamcatcher.event.events;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import ca.retrylife.dreamcatcher.event.EventBase;
import lombok.Data;

@Data
public class RBNEvent extends EventBase {
    String spotter;
    String spotted;
    double frequency;
    String mode;
    int snr;
    Integer speed = null;
    String message = null;
    String grid = null;

    public RBNEvent() {
        super.setType("reverse_beacon_spot");
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }

}