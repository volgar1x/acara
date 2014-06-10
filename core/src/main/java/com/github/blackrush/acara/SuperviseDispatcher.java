package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.dispatch.TypedDispatcher;
import com.github.blackrush.acara.supervisor.event.SupervisedEvent;
import org.fungsi.Either;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static com.github.blackrush.acara.SuperviseListenerMetadataLookup.isSuperviseListener;
import static java.util.Objects.requireNonNull;

/**
 * {@inheritDoc}
 */
public final class SuperviseDispatcher extends TypedDispatcher<SupervisedEvent> {
    /**
     * Lookup {@link com.github.blackrush.acara.SuperviseDispatcher} if given a {@link com.github.blackrush.acara.Supervise} listener metadata
     */
    public static final DispatcherLookup LOOKUP = metadata -> {
        if (isSuperviseListener(metadata.getListenerMethod())) {
            return Optional.of(new SuperviseDispatcher(metadata));
        }

        return Optional.empty();
    };

    private final ListenerMetadata metadata;

    /**
     * Default constructor.
     * @param metadata non-null value
     */
    public SuperviseDispatcher(ListenerMetadata metadata) {
        this.metadata = requireNonNull(metadata, "metadata");
    }

    @Override
    protected Either<Object, Throwable> dispatch0(Object listener, SupervisedEvent event) {
        try {
            return Either.success(metadata.getListenerMethod().invoke(listener, event.getCause(), event.getInitialEvent()));
        } catch (IllegalAccessException e) {
            return Either.failure(e);
        } catch (InvocationTargetException e) {
            return Either.failure(e.getTargetException());
        }
    }
}
