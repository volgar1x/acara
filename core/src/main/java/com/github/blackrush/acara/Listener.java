package com.github.blackrush.acara;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Worker;

public abstract class Listener {
    /**
     * A Listener handles one and only one type of event
     * @return the non-null event's metadata
     */
    public abstract EventMetadata getHandledEvent();

    /**
     * Immediately dispatch an event.
     * @param event a non-null event
     * @param worker a non-null worker
     * @return the pending response
     */
    public abstract Future<Object> dispatch(Object event, Worker worker);
}
