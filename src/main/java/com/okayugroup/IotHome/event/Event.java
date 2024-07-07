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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Event<T> {
    protected Event(String parent, String child, String[] args, EventType type){
        this.name = parent;
        this.child = child;
        this.type = type;
        setArgs(args);
    }
    protected final String name;
    protected final String child;
    protected final EventType type;
    private SizeChangeListener listener;
    public String getParentName() {
        return name;
    }
    public String getChildName() {
        return child;
    }
    @NotNull
    public abstract String[] getArgs();
    public abstract Event<T> setArgs(String... args);
    public abstract Event<T> getCopy();
    public TemplatedEvent getTemplate() {
        return EventController.getTemplate(this);
    }
    @Override
    public String toString() {
        return name + " - " + child;
    }


    /**
     * @param previousResult The result of the previous event
     * @return The result of this event's execution
     */
    public abstract EventResult<T> execute(@Nullable EventResult<?> previousResult);
    public EventResult<?> executeLinkedEvents(@NotNull List<@NotNull LinkedEvent> events, EventResult<?> result) {
        if (events.isEmpty()) return result;  // イベントがリンクされていない場合、自身の結果を折り返す
        EventResult<?> totalResult = null;
        for (LinkedEvent linkedEvent : events) {
            totalResult = linkedEvent.execute(result);
        }
        return totalResult;
    }
    public EventType getTypeId() {
        return type;
    }

    public String getTypicalName() {
        return getClass().getSimpleName();
    }

    public void setSizeChangedListener(SizeChangeListener listener) {
        this.listener = listener;
    }
    public abstract String getReturns();
    public void removeSizeChangedListener() {
        listener = null;
    }

    public SizeChangeListener getSizeChangedListener() {
        return listener;
    }

    private void onSizeChanged(int a) {
        listener.updated(a);
    }

    public interface SizeChangeListener {
        void updated(int maxConnections);
    }
}
