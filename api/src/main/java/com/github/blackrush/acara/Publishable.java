package com.github.blackrush.acara;

import org.fungsi.concurrent.Future;

import java.util.List;

/**
 * {@link com.github.blackrush.acara.Publishable} is the super-interface taking care about publishing events.
 * @see com.github.blackrush.acara.Subscribable
 * @see com.github.blackrush.acara.EventBus
 */
public interface Publishable {
    /**
     * Publish asynchronously an event. This method is therefore not blocking.
     * @param event a non-null event
     * @return a non-null {@link org.fungsi.concurrent.Future} of {@link java.util.List} of {@link java.lang.Object} representing answers of listeners
     */
    Future<List<Object>> publishAsync(Object event);

    /**
     * Publish synchronously an event. This method is therefore actually blocking.
     * @param event a non-null event
     * @return a non-null {@link java.util.List} of {@link java.lang.Object} representing answsers of listeners.
     */
    List<Object> publishSync(Object event);

    /**
     * Publish an event. This method may be asynchronous or synchronous, depending on implementation defaults.
     * @param event a non-null event
     * @return a non-null {@link org.fungsi.concurrent.Future} of {@link java.util.List} of {@link java.lang.Object} representing answers of listeners
     */
    Future<List<Object>> publish(Object event);
}
