package com.okayugroup.IotHome.event;

public abstract class Event {
    protected int typeIndex;
    private final EventTemplate type;
    private final EventTemplate[] templates;
    protected Event(String NAME, int type, String... args){
        this.name = NAME;
        templates = initializeEvents();
        typeIndex = type;
        this.type = templates[type];
        setArgs(args);
    }
    protected Event(int type, String... args){
        this("", type, args);
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
    public abstract Object getArgs();
    public abstract Event setArgs(String... args);

    @Override
    public String toString() {
        return type.name();
    }
    public abstract EventResult execute(EventResult previousResult);
}
