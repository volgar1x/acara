package com.github.blackrush.acara;

public abstract class EventMetadata {

    /**
     * Get the parent.
     * @return the parent or null
     */
    public abstract EventMetadata getParent();

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();
}
