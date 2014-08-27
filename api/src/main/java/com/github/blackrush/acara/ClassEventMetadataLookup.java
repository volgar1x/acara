package com.github.blackrush.acara;

import java.util.Optional;

public interface ClassEventMetadataLookup extends EventMetadataLookup {

    Optional<EventMetadata> lookupClass(Class<?> klass);

    @Override
    default Optional<EventMetadata> lookup(Object event) {
        return lookupClass(event.getClass());
    }
}
