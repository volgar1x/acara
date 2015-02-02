package com.github.blackrush.acara;

public abstract class TypedEventMetadata<T> extends EventMetadata {
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract TypedEventMetadata<? super T> getParent();

    /**
     * Cast down an event to its full type.
     * @param evt a untyped event
     * @return the typed event
     * @deprecated Use with caution!
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public T cast(Object evt) {
        // unsafely cast down for now to avoid the cost of reflection
        // eventbus implementation should be smart enough to dispatch correct events
        return (T) evt;
    }
}
