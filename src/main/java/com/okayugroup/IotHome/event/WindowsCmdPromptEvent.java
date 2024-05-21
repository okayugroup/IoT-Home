package com.okayugroup.IotHome.event;

public class WindowsCmdPromptEvent extends CommandEvent {
    public WindowsCmdPromptEvent(String... args) {
        super("cmd.exe /c" + args[0]);
    }
}
