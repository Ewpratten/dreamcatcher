package ca.retrylife.dreamcatcher.event;

import lombok.Data;

@Data
public abstract class EventBase {

    String type;
    long timestamp;

    public abstract String toJson();
    
}