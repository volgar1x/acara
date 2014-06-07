package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.Dispatcher;
import com.github.blackrush.acara.dispatch.DispatcherLookup;
import org.fungsi.Either;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static org.fungsi.Either.failure;
import static org.fungsi.Either.success;

public class StdDispatcher implements Dispatcher {
    public static final DispatcherLookup LOOKUP = metadata -> Optional.of(new StdDispatcher(metadata));

    final ListenerMetadata metadata;

    public StdDispatcher(ListenerMetadata metadata) {
        this.metadata = metadata;
    }

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
