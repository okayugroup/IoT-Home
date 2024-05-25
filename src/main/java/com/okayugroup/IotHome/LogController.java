package com.okayugroup.IotHome;

public class LogController {
    public enum LogLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }
    public static LogController LOGGER;
    private final MainView view;
    public LogController(MainView view){
        this.view = view;
        LOGGER = this;
    }
    public void log(String message) {
        view.setLog(LogLevel.INFO, message);
    }
    public void log(LogLevel level, String message) {
        view.setLog(level, message);
    }

}
