package com.github.blackrush.acara.supervisor;

import com.github.blackrush.acara.ListenerMetadata;
import com.github.blackrush.acara.dispatch.TypedDispatcher;
import com.github.blackrush.acara.supervisor.event.SupervisedEvent;
import org.fungsi.Either;

import java.lang.reflect.InvocationTargetException;

/**
 * {@inheritDoc}
 */
public final class SuperviseDispatcher extends TypedDispatcher<SupervisedEvent> {
    private final ListenerMetadata metadata;

    /**
     * Default constructor.
     * @param metadata non-null value
     */
    public SuperviseDispatcher(ListenerMetadata metadata) {
        this.metadata = metadata;
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
