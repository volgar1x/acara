package com.github.blackrush.acara;

import java.util.Optional;
import java.util.stream.Stream;

import static com.github.blackrush.acara.StreamUtils.asStream;
import static com.github.blackrush.acara.StreamUtils.directParent;

/**
 * {@inheritDoc}
 *
 */
public final class StdEventMetadata implements EventMetadata {
    /**
     * Always return a new {@link com.github.blackrush.acara.StdEventMetadata}
     */
    public static final ClassEventMetadataLookup LOOKUP = new ClassEventMetadataLookup() {
        @Override
        public Optional<EventMetadata> lookupClass(Class<?> klass) {
            return Optional.of(new StdEventMetadata(klass, this));
        }
    };

    final static class CachingLookup extends CachingEventMetadataLookup implements ClassEventMetadataLookup {
        @Override
        public Optional<EventMetadata> lookupClass(Class<?> klass) {
            return Optional.of(new StdEventMetadata(klass, this));
        }

        @Override
        protected Optional<EventMetadata> lookup0(Object evt) {
            return lookupClass(evt.getClass());
        }
    }

    public static EventMetadataLookup createCachingLookup() {
        return new CachingLookup();
    }

    private final Class<?> eventClass;
    private final ClassEventMetadataLookup lookup;

    /**
     * Default constructor.
     * @param eventClass non-null event's class
     * @param lookup non-null event metadata lookup
     */
    public StdEventMetadata(Class<?> eventClass, ClassEventMetadataLookup lookup) {
        this.eventClass = eventClass;
        this.lookup = lookup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getRawEventClass() {
        return eventClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<EventMetadata> getParent() {
        return directParent(eventClass, Object.class).flatMap(parent -> asStream(lookup.lookupClass(parent)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Object event) {
        return eventClass.isInstance(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StdEventMetadata that = (StdEventMetadata) o;

        return eventClass.equals(that.eventClass);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return eventClass.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "StdEventMetadata(" +
                "eventClass=" + eventClass +
                ')';
    }
}
