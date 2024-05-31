package com.okayugroup.IotHome;

import org.jetbrains.annotations.Nullable;

public class LogController {
    public enum LogLevel {
        DEBUG,
        INFO,
        ERROR
    }
    public static LogController LOGGER = new LogController(null);
    @Nullable
    private final MainView view;
    public LogController(@Nullable MainView view){
        this.view = view;
        LOGGER = this;
    }
    public void log(String message) {
        log(LogLevel.INFO, message);
    }
    public void log(LogLevel level, String message) {
        if (view != null)
            view.setLog(level, message);
    }

}
