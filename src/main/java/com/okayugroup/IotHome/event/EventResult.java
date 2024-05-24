package com.okayugroup.IotHome.event;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record EventResult(@Nullable EventTemplate type, int endCode, List<String> result){
    public static final EventResult ERROR = new EventResult(null, -1, List.of());
}
