package com.okayugroup.IotHome.event;

import java.io.IOException;

public class WindowsPowershellEvent extends CommandEvent {
    public WindowsPowershellEvent(String... args) {
        super("powershell.exe /c " + args[0]);
    }

}
