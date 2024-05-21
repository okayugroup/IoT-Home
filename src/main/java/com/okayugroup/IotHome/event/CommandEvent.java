package com.okayugroup.IotHome.event;

import java.io.IOException;

public class CommandEvent extends Event {
    public CommandEvent(String... args) {
        super(args);
    }

    protected String command;

    @Override
    protected EventType getThisType() {
        return EventType.COMMAND;
    }
    @Override
    public String getName(){
        return "コマンドを実行する";
    }

    @Override
    public void setArgs(String... args) {
        setArgs(args[0]);
    }
    public void setArgs(String arg) {
        command = arg;
    }

    @Override
    public EventResult execute(EventResult previousResult) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        return new EventResult(type, process.exitValue(), process.children());
    }
}
