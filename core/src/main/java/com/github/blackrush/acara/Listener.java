package com.github.blackrush.acara;

import org.fungsi.concurrent.Future;

public abstract class Listener {
    /**
     *
     * @return
     */
    public abstract EventMetadata getHandledEvent();

    /**
     * Immediately dispatch an event.
     * @param event a non-null event
     * @return the pending response
     */
    public abstract Future<Object> dispatch(Object event);
}
