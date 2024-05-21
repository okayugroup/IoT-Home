package com.okayugroup.IotHome.event;

public class WindowsPowershellEvent extends CommandEvent {
    public WindowsPowershellEvent(String... args) {
        super(args);
    }

    @Override
    public void setArgs(String arg) {
        super.setArgs("powershell.exe /c " + arg);
    }

    @Override
    public String getName() {
        return "Powershell" + super.getName();
    }

}
