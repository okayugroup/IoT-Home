package com.okayugroup.IotHome.event;

import jakarta.annotation.Nullable;

public record EventTemplate(String name, int argc, @Nullable String... argDescription) {
    @Override
    public String toString() {
        return name;
    }
}