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

package com.okayugroup.IotHome.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Eventに表示とリンク機能を加えたもの。それぞれ固有のEventをもつ。
 */
public class LinkedEvent {
    private final Event<?> event;
    private final List<LinkedEvent> events = new ArrayList<>();
    private double x;
    private double y;
    private double width;
    private double height;
    private int maxConnections = 1;
    private boolean isAsync;

    public LinkedEvent(Event<?> event, double x, double y, double width, double height, boolean isAsync, LinkedEvent... events) {
        this.event = event;
        event.setSizeChangedListener(this::setMaxConnections);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isAsync = isAsync;
        Collections.addAll(this.events, events);
    }
    public Event<?> getEvent() {
        return event;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public List<LinkedEvent> getEvents() {
        return events;
    }

    public EventResult<?> execute() {
        return execute(null);
    }

    public EventResult<?> execute(EventResult<?> result) {
        if (isAsync) {
            new Thread(() -> event.execute(result)).start();
            return event.executeLinkedEvents(events, result);
        } else {
            EventResult<?> resultNext = event.execute(result);
            return event.executeLinkedEvents(events, resultNext);
        }
    }

    private void setMaxConnections(int value) {
        maxConnections = value;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    @Override
    public String toString() {
        return event.toString() + "(" + width + " * " + height + ", [" + x + ", " + y + "])" + ")";
    }

    public void setArgs(String... args) {
        event.setArgs(args);
        EventController.setEventDict(this);
    }

    public String[] getArgs() {
        return event.getArgs();
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }
}
