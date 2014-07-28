package com.github.blackrush.acara;

import java.util.Collection;

/**
 * An {@link com.github.blackrush.acara.EventBus} is {@link com.github.blackrush.acara.Publishable} and {@link com.github.blackrush.acara.Subscribable}
 * @see com.github.blackrush.acara.Publishable
 * @see com.github.blackrush.acara.Subscribable
 */
public interface EventBus extends Publishable, Subscribable {
    /**
     * {@inheritDoc}
     */
    @Override
    EventBus subscribe(Object subscriber);

    /**
     * {@inheritDoc}
     */
    @Override
    EventBus subscribeMany(Collection<?> subscribers);
}
