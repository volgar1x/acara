package com.github.blackrush.acara;

@FunctionalInterface
public interface EventModule {
    EventBusBuilder configure(EventBusBuilder builder);

    default EventModule andThen(EventModule other) {
        return builder -> other.configure(this.configure(builder));
    }

    default EventModule compose(EventModule other) {
        return builder -> this.configure(other.configure(builder));
    }

    static final EventModule UNIT = builder -> builder;

    public static EventModule unit() {
        return UNIT;
    }

    public static EventModule unitModule() {
        return unit();
    }
}
