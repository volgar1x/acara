package com.github.blackrush.acara;

import com.github.blackrush.acara.supervisor.event.SupervisedEvent;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * {@inheritDoc}
 */
public final class SupervisedEventMetadata implements EventMetadata {
    /**
     * Lookup a {@link com.github.blackrush.acara.SupervisedEventMetadata} if given event instance is an instance of {@link com.github.blackrush.acara.supervisor.event.SupervisedEvent}
     */
    public static final EventMetadataLookup LOOKUP = event -> {
        if (event instanceof SupervisedEvent) {
            SupervisedEvent evt = (SupervisedEvent) event;

            @SuppressWarnings("unchecked")
            Class<Throwable> handledCauseClass = (Class) evt.getCause().getClass();

            Class<?> handledInitialEventClass = evt.getInitialEvent().getClass();

            return Optional.of(new SupervisedEventMetadata(handledCauseClass, handledInitialEventClass));
        }

        return Optional.empty();
    };

    final Class<?> handledCauseClass;
    final Class<?> handledInitialEventClass;

    /**
     * Default constructor.
     * @param handledCauseClass non-null class
     * @param handledInitialEventClass non-null class
     */
    public SupervisedEventMetadata(Class<?> handledCauseClass, Class<?> handledInitialEventClass) {
        this.handledCauseClass = requireNonNull(handledCauseClass, "handledCauseClass");
        this.handledInitialEventClass = requireNonNull(handledInitialEventClass, "handledInitialEventClass");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getRawEventClass() {
        return SupervisedEvent.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<EventMetadata> getParent() {
        Optional<Class<?>> causeParent = StreamUtils.optionalParent(handledCauseClass, Object.class);
        Optional<Class<?>> eventParent = StreamUtils.optionalParent(handledInitialEventClass, null);

        Stream.Builder<EventMetadata> builder = Stream.builder();

        causeParent.map(parent -> new SupervisedEventMetadata(parent, handledInitialEventClass)).ifPresent(builder);
        eventParent.map(parent -> new SupervisedEventMetadata(handledCauseClass, parent)).ifPresent(builder);
        causeParent.flatMap(a -> eventParent.map(b -> new SupervisedEventMetadata(a, b))).ifPresent(builder);

        return builder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Object event) {
        if (!(event instanceof SupervisedEvent)) {
            return false;
        }

        SupervisedEvent evt = (SupervisedEvent) event;

        return handledCauseClass.isInstance(evt.getCause()) &&
               handledInitialEventClass.isInstance(evt.getInitialEvent());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("all")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupervisedEventMetadata that = (SupervisedEventMetadata) o;

        if (!handledCauseClass.equals(that.handledCauseClass)) return false;
        if (!handledInitialEventClass.equals(that.handledInitialEventClass)) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("all")
    @Override
    public int hashCode() {
        int result = SupervisedEvent.class.hashCode();
        result = 31 * result + handledCauseClass.hashCode();
        result = 31 * result + handledInitialEventClass.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SupervisedEventMetadata(" +
                "handledCauseClass=" + handledCauseClass +
                ", handledInitialEventClass=" + handledInitialEventClass +
                ')';
    }
}
