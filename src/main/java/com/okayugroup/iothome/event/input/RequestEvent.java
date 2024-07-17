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

package com.okayugroup.iothome.event.input;

import com.okayugroup.iothome.event.Event;
import com.okayugroup.iothome.event.EventResult;
import com.okayugroup.iothome.event.InputEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequestEvent extends InputEvent<Boolean> {
    public RequestEvent(String... args) {
        super("リクエスト", "GETハンドリング", args);
    }

    public String category, field;
    @Override
    public String getReturns() {
        return "常にTrue\n真偽値";
    }
    @Override
    public @NotNull String[] getArgs() {
        return new String[]{field, category};
    }

    @Override
    protected void setArgs(String... args) {
        field = args.length > 0 ? args[0] : "";
        category = args.length > 1? args[1] : "";
    }

    @Override
    public Event<Boolean> getCopy() {
        return new RequestEvent();
    }

    @Override
    public EventResult<Boolean> execute(@Nullable EventResult<?> previousResult) {
        return new EventResult<>(null, true);
    }
}
