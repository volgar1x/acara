package com.github.blackrush.acara;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CachingEventMetadataLookup implements EventMetadataLookup {
    private final EventMetadataLookup lookup;
    private final Map<Class<?>, Optional<EventMetadata>> cache;

    public CachingEventMetadataLookup(EventMetadataLookup lookup, Map<Class<?>, Optional<EventMetadata>> cache) {
        this.lookup = lookup;
        this.cache = cache;
    }

    public CachingEventMetadataLookup(EventMetadataLookup lookup) {
        this(lookup, new ConcurrentHashMap<>());
    }

    protected CachingEventMetadataLookup() {
        this(null);
    }

    protected Optional<EventMetadata> lookup0(Object evt) {
        return lookup.lookup(evt);
    }

    @Override
    public Optional<EventMetadata> lookup(Object event) {
        return cache.computeIfAbsent(event.getClass(), this::lookup0);
    }
}
