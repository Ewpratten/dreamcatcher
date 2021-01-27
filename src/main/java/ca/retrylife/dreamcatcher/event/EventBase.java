package ca.retrylife.dreamcatcher.event;

public abstract class EventBase {

    public String type;

    public abstract String toJson();
    
}