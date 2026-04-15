package com.chapt003.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    int maxRequests() default 5;
    long timeWindow() default 60000;
}