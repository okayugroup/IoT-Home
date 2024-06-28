package com.okayugroup.IotHome.event;

public abstract class TemporaryEvent<T> extends Event<T> {

    protected TemporaryEvent(String parent, String child, String... args) {
        super(parent, child, args, EventType.TEMPORARY);
    }
}
