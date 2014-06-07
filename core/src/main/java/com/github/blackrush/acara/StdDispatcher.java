package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.Dispatcher;
import com.github.blackrush.acara.dispatch.DispatcherLookup;
import org.fungsi.Either;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.fungsi.Either.failure;
import static org.fungsi.Either.success;

/**
 * {@inheritDoc}
 * {@link com.github.blackrush.acara.StdDispatcher} works great for {@link com.github.blackrush.acara.Listener} listeners.
 * @see com.github.blackrush.acara.Listener
 */
public class StdDispatcher implements Dispatcher {
    /**
     * A shareable {@link com.github.blackrush.acara.dispatch.DispatcherLookup} returning every time a new instance of {@link com.github.blackrush.acara.StdDispatcher}
     */
    public static final DispatcherLookup LOOKUP = metadata -> Optional.of(new StdDispatcher(metadata));

    final ListenerMetadata metadata;

    /**
     * Default constructor.
     * @param metadata a non-null {@link com.github.blackrush.acara.ListenerMetadata} used to dispatch events
     */
    public StdDispatcher(ListenerMetadata metadata) {
        requireNonNull(metadata, "metadata");

        if (!StdListenerMetadataLookup.isValidListener(metadata.getListenerMethod())) {
            throw new IllegalStateException(metadata.getListenerMethod() + " is not a valid listener");
        }

        this.metadata = metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<Object, Throwable> dispatch(Object listener, Object event) {
        try {
            return success(metadata.getListenerMethod().invoke(listener, event));
        } catch (IllegalAccessException e) {
            return failure(e);
        } catch (InvocationTargetException e) {
            return failure(e.getTargetException());
        }
    }
}
