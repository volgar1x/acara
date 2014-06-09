package com.github.blackrush.acara;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * {@link com.github.blackrush.acara.EventMetadataLookup} is responsible of looking for an applicable {@link com.github.blackrush.acara.EventMetadata}
 * instance for a given event.
 * @see com.github.blackrush.acara.EventMetadata
 */
@FunctionalInterface
public interface EventMetadataLookup {
    /**
     * Lookup an applicable {@link com.github.blackrush.acara.EventMetadata} for a given {@link java.lang.Object} event
     * @param event a non-null value
     * @return a non-null option
     */
    Optional<EventMetadata> lookup(Object event);

    /**
     * Fold two {@link com.github.blackrush.acara.EventMetadataLookup} into one.
     * @param other a non-null value
     * @return a non-null value
     */
    default EventMetadataLookup withFallback(EventMetadataLookup other) {
        requireNonNull(other, "other");
        return event -> {
            Optional<EventMetadata> opt = this.lookup(event);

            if (opt.isPresent()) {
                return opt;
            }

            return other.lookup(event);
        };
    }

    /**
     * Convert looked-up {@link com.github.blackrush.acara.EventMetadata} to another {@link com.github.blackrush.acara.EventMetadata}
     * @param fn a non-null {@link java.util.function.Function}
     * @return a non-null {@link com.github.blackrush.acara.EventMetadataLookup}
     */
    default EventMetadataLookup bind(Function<EventMetadata, Optional<EventMetadata>> fn) {
        requireNonNull(fn, "fn");
        return event -> this.lookup(event).flatMap(fn);
    }
}
