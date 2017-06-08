package de.ellpeck.rockbottom.api.event;

public enum EventResult{
    DEFAULT,
    MODIFIED,
    CANCELLED;

    public boolean shouldCancel(){
        return this == CANCELLED;
    }
}
