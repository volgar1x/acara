package com.github.blackrush.acara;

@FunctionalInterface
public interface EventModule {
    EventBusBuilder configure(EventBusBuilder builder);
}
