package ca.retrylife.dreamcatcher.event.events;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import ca.retrylife.dreamcatcher.event.EventBase;
import lombok.Data;

@Data
public class DXWatchEvent extends EventBase {
    String spotter;
    String spotted;
    double frequency;
    String message = null;
    String grid = null;

    public DXWatchEvent() {
        super.setType("dxwatch");
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }

}