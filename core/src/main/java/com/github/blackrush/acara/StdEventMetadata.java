package com.github.blackrush.acara;

import java.util.Optional;
import java.util.stream.Stream;

import static com.github.blackrush.acara.StreamUtils.directParent;
import static java.util.Objects.requireNonNull;

/**
 * {@inheritDoc}
 *
 */
public final class StdEventMetadata implements EventMetadata {
    /**
     * Always return a new {@link com.github.blackrush.acara.StdEventMetadata}
     */
    public static final EventMetadataLookup LOOKUP = event -> Optional.of(new StdEventMetadata(event.getClass()));

    private final Class<?> eventClass;

    /**
     * Default constructor.
     * @param eventClass non-null event's class
     */
    public StdEventMetadata(Class<?> eventClass) {
        this.eventClass = requireNonNull(eventClass, "eventClass");
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
        return directParent(eventClass, Object.class).map(StdEventMetadata::new);
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
