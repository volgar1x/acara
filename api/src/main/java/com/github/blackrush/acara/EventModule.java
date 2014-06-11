package com.github.blackrush.acara;

import static java.util.Objects.requireNonNull;

/**
 * One {@link com.github.blackrush.acara.EventModule} may configure an {@link com.github.blackrush.acara.EventBusBuilder}
 */
@FunctionalInterface
public interface EventModule {
    /**
     * Configure a {@link com.github.blackrush.acara.EventBusBuilder}
     * @param builder non-null builder
     * @return a non-null builder
     */
    EventBusBuilder configure(EventBusBuilder builder);

    /**
     * Aggregate this {@link com.github.blackrush.acara.EventModule} to another one.
     * This instance will configure one {@link com.github.blackrush.acara.EventBusBuilder} first.
     * @param other a non-null module
     * @return a non-null module
     */
    default EventModule andThen(EventModule other) {
        requireNonNull(other, "other");
        return builder -> other.configure(this.configure(builder));
    }

    /**
     * Aggregate this {@link com.github.blackrush.acara.EventModule} to another one.
     * The other instance will configure {@link com.github.blackrush.acara.EventBusBuilder} first.
     * @param other a non-null module
     * @return a non-null module
     */
    default EventModule compose(EventModule other) {
        requireNonNull(other, "other");
        return builder -> this.configure(other.configure(builder));
    }

    static final EventModule UNIT = builder -> builder;

    /**
     * An {@link com.github.blackrush.acara.EventModule} configuring nothing.
     * @return a non-null module
     */
    public static EventModule unit() {
        return UNIT;
    }

    /**
     * An {@link com.github.blackrush.acara.EventModule} configuring nothing.
     * @return a non-null module
     */
    public static EventModule unitModule() {
        return unit();
    }
}
