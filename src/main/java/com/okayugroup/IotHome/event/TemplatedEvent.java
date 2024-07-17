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

public class TemplatedEvent {
    private final Event<?> event;
    private final String[] argDescriptions;
    public TemplatedEvent(Event<?> event, String[] argDescriptions) {
        this.event = event;
        this.argDescriptions = argDescriptions;
    }

    public String[] getArgDescriptions() {
        return argDescriptions;
    }

    public Event<?> getNew() {
        return event.getCopy();
    }
}
