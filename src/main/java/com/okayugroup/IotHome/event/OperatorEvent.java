package com.okayugroup.IotHome.event;

public abstract class OperatorEvent<T> extends Event<T> {

    protected OperatorEvent(String parent, String child, String... args) {
        super(parent, child, args, EventType.OPERATOR);
    }
}
