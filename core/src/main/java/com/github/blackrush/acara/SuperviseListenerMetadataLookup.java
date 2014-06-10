package com.github.blackrush.acara;

import java.util.stream.Stream;

/**
 * {@inheritDoc}
 */
public final class SuperviseListenerMetadataLookup implements ListenerMetadataLookup {

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<ListenerMetadata> lookup(Object listener) {
        throw new Error("not implemented"); // TODO
    }
}
