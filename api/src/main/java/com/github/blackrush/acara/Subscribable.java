package com.github.blackrush.acara;

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
    Subscribable subscribe(Object listener);

    /**
     * Unsubscribe a listener.
     * @param listener a non-null listener
     * @return {@value true} if the listener has been successfully unsubscribed
     */
    boolean unsubscribe(Object listener);
}