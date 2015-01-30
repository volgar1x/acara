package com.github.blackrush.acara;

public abstract class EventMetadata {

    /**
     * Get the parent.
     * @return the parent or null
     */
    public abstract EventMetadata getParent();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(Object other);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode();
}
