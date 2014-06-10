package com.github.blackrush.acara;

import java.util.stream.Stream;

/**
 * {@link com.github.blackrush.acara.EventMetadata} provides metadata about published events. Implementations should be
 * immutable and implementing equals/hashCode.
 */
public interface EventMetadata {
    /**
     * Get raw {@link java.lang.Class} of handled event.
     * @return a non-null value
     */
    Class<?> getRawEventClass();

    /**
     * Get parents of this {@link com.github.blackrush.acara.EventMetadata}
     * @return a non-null stream
     */
    Stream<EventMetadata> getParent();

    /**
     * Test if this {@link com.github.blackrush.acara.EventMetadata} applies to a given event instance.
     * @param event a nullable value
     * @return {@literal true} if this {@link com.github.blackrush.acara.EventMetadata} applies, {@literal false} otherwise
     */
    boolean accept(Object event);

    /**
     * Get hash of this {@link com.github.blackrush.acara.EventMetadata}. Used in hash tables.
     * @return an integer
     */
    @Override
    int hashCode();

    /**
     * Test if this {@link com.github.blackrush.acara.EventMetadata} equals to another {@link java.lang.Object}. Used in hash tables.
     * @param other a nullable value
     * @return {@literal true} if it is equal, {@literal false} otherwise
     */
    @Override
    boolean equals(Object other);
}
