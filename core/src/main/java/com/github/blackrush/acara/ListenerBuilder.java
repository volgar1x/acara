package com.github.blackrush.acara;

@FunctionalInterface
public interface ListenerBuilder {
    /**
     * Build the listener given its instance.
     * @param o a non-null instance
     * @return the builded listener or null if the builder does not care
     */
    Listener build(Object o);

    default ListenerBuilder withFallback(ListenerBuilder other) {
        return o -> {
            Listener listener = this.build(o);
            if (listener != null) {
                return listener;
            }
            return other.build(o);
        };
    }
}
