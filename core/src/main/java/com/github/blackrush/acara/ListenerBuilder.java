package com.github.blackrush.acara;

import java.util.stream.Stream;

@FunctionalInterface
public interface ListenerBuilder {
    /**
     * Build the listener given its instance.
     * @param o a non-null instance
     * @return the built listener or null if the builder does not care
     */
    Stream<Listener> build(Object o);

    default ListenerBuilder concat(ListenerBuilder other) {
        return o -> Stream.concat(this.build(o), other.build(o));
    }
}
