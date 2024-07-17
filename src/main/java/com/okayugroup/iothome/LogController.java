/*
 * This file is part of Iot-Home.
 *
 * Iot-Home is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iot-Home is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Iot-Home. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2024 OkayuGroup
 */

package com.okayugroup.iothome;

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
