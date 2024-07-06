package com.okayugroup.IotHome.event.input;

import com.okayugroup.IotHome.event.EventResult;
import com.okayugroup.IotHome.event.InputEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class WebRequestEvent<T> extends InputEvent<T> {
    protected WebRequestEvent(String name, String... args) {
        super("HTTPリクエスト", name, args);
    }
    protected String category, field;
    @Override
    public @NotNull String @NotNull [] getArgs() {
        return new String[]{category, field};
    }

    public String getCategory() {
        return category;
    }

    public String getField() {
        return field;
    }

    @Override
    public WebRequestEvent<T> setArgs(String... args) {
        category = args[0];
        field = args[1];
        return this;
    }


    @Override
    public abstract WebRequestEvent<T> getCopy();
    public static class GetRequest extends WebRequestEvent<Boolean> {
        protected GetRequest() {
            super("GETリクエスト");
        }

        @Override
        public EventResult<Boolean> execute(@Nullable EventResult<?> previousResult) {
            return new EventResult<>(null, true);
        }

        @Override
        public WebRequestEvent<Boolean> getCopy() {
            return new GetRequest();
        }
    }
}
