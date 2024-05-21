package com.okayugroup.IotHome.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// アノテーションの保持期間を指定
@Retention(RetentionPolicy.RUNTIME)
// アノテーションの適用対象を指定
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface EventName {
    String name();
}
