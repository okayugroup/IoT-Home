/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.okayugroup.IotHome.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkedEvent {
    private final Event<?> event;
    private final List<LinkedEvent> events = new ArrayList<>();
    private double x;
    private double y;
    private double width;
    private double height;
    private int maxConnections;

    public LinkedEvent(Event<?> event, double x, double y, double width, double height, LinkedEvent... events) {
        this.event = event;
        event.setSizeChangedListener(this::setMaxConnections);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
        EventResult<?> resultNext = event.execute(result);
        return event.executeLinkedEvents(events, resultNext);
    }

    private void setMaxConnections(int value) {
        maxConnections = value;
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}
