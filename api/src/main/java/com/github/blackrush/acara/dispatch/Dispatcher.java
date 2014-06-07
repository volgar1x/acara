package com.github.blackrush.acara.dispatch;

import org.fungsi.Either;

/**
 * {@link com.github.blackrush.acara.dispatch.Dispatcher} dispatches a given event to a given listener, and then return its response as a {@link org.fungsi.Either}
 */
public interface Dispatcher {
    /**
     * Dispatch an event to a listener.
     * @param listener a non-null listener
     * @param event a non-null event
     * @return a non-null response
     */
    Either<Object, Throwable> dispatch(Object listener, Object event);
}
