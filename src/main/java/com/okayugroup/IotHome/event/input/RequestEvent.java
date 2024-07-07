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

package com.okayugroup.IotHome.event.input;

import com.okayugroup.IotHome.event.Event;
import com.okayugroup.IotHome.event.EventResult;
import com.okayugroup.IotHome.event.InputEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequestEvent extends InputEvent<Boolean> {
    public RequestEvent(String... args) {
        super("リクエスト", "GETハンドリング", args);
    }

    public String category, field;

    @Override
    public @NotNull String[] getArgs() {
        return new String[]{category, field};
    }

    @Override
    public Event<Boolean> setArgs(String... args) {
        field = args.length > 0 ? args[0] : "name";
        category = args.length > 1? args[1] : "dir";
        return this;
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
