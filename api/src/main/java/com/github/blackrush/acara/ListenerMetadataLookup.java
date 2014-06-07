package com.github.blackrush.acara;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * {@link com.github.blackrush.acara.ListenerMetadataLookup} give multiple {@link com.github.blackrush.acara.ListenerMetadata}
 * for a given listener.
 */
@FunctionalInterface
public interface ListenerMetadataLookup {
    /**
     * Lookup multiple {@link com.github.blackrush.acara.ListenerMetadata} for a given listener
     * @param listener a non-null listener
     * @return a non-null stream
     */
    Stream<ListenerMetadata> lookup(Object listener);

    /**
     * Fold two {@link com.github.blackrush.acara.ListenerMetadataLookup} into one.
     * @param other a non-null {@link com.github.blackrush.acara.ListenerMetadataLookup}
     * @return a non-null {@link com.github.blackrush.acara.ListenerMetadataLookup}
     */
    default ListenerMetadataLookup concat(ListenerMetadataLookup other) {
        requireNonNull(other, "other");
        return listener -> Stream.concat(this.lookup(listener), other.lookup(listener));
    }
}
