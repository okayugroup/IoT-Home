package com.okayugroup.IotHome.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;


public record EventResult<T>(@Nullable Exception exception, T result){
    @Contract(pure = true)
    public boolean isError() {
        return exception != null;
    }
    public Class<?> getReturns(){
        return result.getClass();
    }
}
