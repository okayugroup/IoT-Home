package com.okayugroup.IotHome.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Event<T> {
    protected Event(String parent, String child, String[] args, EventType type){
        this.name = parent;
        this.child = child;
        this.type = type;
        setArgs(args);
    }
    protected final String name;
    protected final String child;
    protected final EventType type;
    public String getParentName() {
        return name;
    }
    public String getChildName() {
        return child;
    }
    public abstract String[] getArgs();
    public abstract Event<T> setArgs(String... args);
    public abstract Event<T> getCopy();

    @Override
    public String toString() {
        return name + " - " + child;
    }
    public abstract EventResult<T> execute(@Nullable EventResult<?> previousResult);
    public EventResult<?> executeLinkedEvents(LinkedEvent @NotNull [] events, EventResult<?> result) {
        if (events.length == 0) return result;  // イベントがリンクされていない場合、自身の結果を折り返す
        EventResult<?> totalResult = null;
        for (LinkedEvent linkedEvent : events) {
            totalResult = linkedEvent.execute(result);
        }
        return totalResult;
    }
    public EventType getTypeId() {
        return type;
    }
}
