package com.okayugroup.IotHome.event;

public class TemplatedEvent {
    private final Event<?> event;
    private final String[] argDescriptions;
    public TemplatedEvent(Event<?> event, String[] argDescriptions) {
        this.event = event;
        this.argDescriptions = argDescriptions;
    }

    public String[] getArgDescriptions() {
        return argDescriptions;
    }

    public Event<?> getNew() {
        return event.getCopy();
    }
}
