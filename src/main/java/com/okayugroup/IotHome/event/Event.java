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

import org.jetbrains.annotations.Nullable;

public abstract class Event {
    protected final int typeIndex;
    private final EventTemplate type;
    private final EventTemplate[] templates;
    protected Event(String NAME, int type, String... args){
        this.name = NAME;
        templates = initializeEvents();
        typeIndex = type;
        this.type = templates[type];
        setArgs(args);
    }
    protected abstract EventTemplate[] initializeEvents();
    public EventTemplate getType() {
        return type;
    }
    public abstract Event getCopy(int typeIndex);
    public Event getCopy() {
        return getCopy(typeIndex);
    }
    public final String name;
    public EventTemplate[] getTemplates() {
        return templates;
    }
    public abstract String[] getArgs();
    public abstract Event setArgs(String... args);

    @Override
    public String toString() {
        return type.name();
    }
    public abstract EventResult execute(@Nullable EventResult previousResult);
}
