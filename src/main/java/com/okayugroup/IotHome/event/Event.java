package com.okayugroup.IotHome.event;

import org.jetbrains.annotations.Nullable;

public abstract class Event {
    protected final int typeIndex;
    private final EventTemplate type;
    private final EventTemplate[] templates;
    protected Event(String NAME, int type, String... args){
        this.name = NAME;
        templates = initializeEvents();
        typeIndex = type;
        this.type = templates[type];
        setArgs(args);
    }
    protected abstract EventTemplate[] initializeEvents();
    public EventTemplate getType() {
        return type;
    }
    public abstract Event getCopy(int typeIndex);
    public Event getCopy() {
        return getCopy(typeIndex);
    }
    public final String name;
    public EventTemplate[] getTemplates() {
        return templates;
    }
    public abstract String[] getArgs();
    public abstract Event setArgs(String... args);

    @Override
    public String toString() {
        return type.name();
    }
    public abstract EventResult execute(@Nullable EventResult previousResult);
}
