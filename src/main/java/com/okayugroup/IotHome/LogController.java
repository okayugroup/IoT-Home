package com.okayugroup.IotHome;

import javax.swing.*;
import java.util.logging.Level;

public class LogController {
    public enum LogLevel {
        INFO,
        WARNING,
        ERROR
    }
    public static LogController LOG;
    private final MainView view;
    public LogController(MainView view){
        this.view = view;
        LOG = this;
    }
    public void addLog(String message) {
        view.setLog(LogLevel.INFO, message);
    }

}
