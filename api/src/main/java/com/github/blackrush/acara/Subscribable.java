package com.github.blackrush.acara;

import java.util.Collection;

/**
 * {@link com.github.blackrush.acara.Subscribable} is the super-interface taking care about subscriptions of listeners.
 * @see com.github.blackrush.acara.Publishable
 * @see com.github.blackrush.acara.EventBus
 */
public interface Subscribable {
    /**
     * Subscribe a listener.
     * @param listener a non-null listener
     * @return this very same instance
     */
    Subscription subscribe(Object listener);

    /**
     * Subscribe multiple listeners.
     * @param listeners a non-null collection of non-null listeners
     * @return this very same instance
     */
    Subscription subscribeMany(Collection<?> listeners);
}
