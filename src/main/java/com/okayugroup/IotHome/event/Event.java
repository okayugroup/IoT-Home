package com.okayugroup.IotHome.event;

import java.io.IOException;

public abstract class Event {
    public Event(String... args){
        this();
        setArgs(args);
    }
    public Event() {
        type = getThisType();
    }
    protected final EventType type;

    protected abstract EventType getThisType();
    public enum EventType {
        COMMAND,
        EXEC_FILE,

        InternetRequest,
        PLAY_SOUND,
        Condition,
    }

    public EventType getType() {
        return type;
    }
    public abstract String getName();
    public abstract void setArgs(String... args);
    @Override
    public String toString() {
        return getName();
    }
    public abstract EventResult execute(EventResult previousResult) throws IOException, InterruptedException;
}
