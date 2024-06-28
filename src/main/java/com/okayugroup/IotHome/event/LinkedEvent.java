package com.okayugroup.IotHome.event;


public class LinkedEvent {
    private final Event<?> event;
    private LinkedEvent[] events = new LinkedEvent[] {};
    public LinkedEvent(Event<?> event) {
        this.event = event;
    }
    public Event<?> getEvent() {
        return event;
    }
    public void setEvents(LinkedEvent[] events) {
        this.events = events;
    }
    public LinkedEvent[] redirect() {
        return events;
    }

    public EventResult<?> execute() {
        return execute(null);
    }

    public EventResult<?> execute(EventResult<?> result) {
        EventResult<?> resultNext = event.execute(result);
        return event.executeLinkedEvents(events, resultNext);
    }
}
