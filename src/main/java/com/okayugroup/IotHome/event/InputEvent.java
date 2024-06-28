package com.okayugroup.IotHome.event;

public abstract class InputEvent<T> extends Event<T> {

    protected InputEvent(String parent, String child, String... args) {
        super(parent, child, args, EventType.INPUT);
    }
}
