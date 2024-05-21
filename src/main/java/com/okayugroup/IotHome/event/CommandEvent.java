package com.okayugroup.IotHome.event;

import java.io.IOException;

public class CommandEvent extends Event {
    protected final String command;
    public CommandEvent(String command) {
        super();
        this.command = command;
    }

    @Override
    protected EventType getThisType() {
        return EventType.COMMAND;
    }

    @Override
    public EventResult execute(EventResult previousResult) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        return new EventResult(type, process.exitValue(), process.children());
    }
}
