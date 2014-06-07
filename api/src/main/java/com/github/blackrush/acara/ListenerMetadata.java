package com.github.blackrush.acara;

import java.lang.reflect.Method;

/**
 * {@link com.github.blackrush.acara.ListenerMetadata} holds all useful listener metadata.
 *
 * {@link com.github.blackrush.acara.ListenerMetadata} is a pure immutable type, implementing equals/hashCode.
 */
public final class ListenerMetadata {
    private final Class<?> listenerClass;
    private final Method listenerMethod;
    private final Class<?> handledEventClass;

    /**
     * Default constructor.
     * @param listenerClass a non-null {@link java.lang.Class} representing listener's class
     * @param listenerMethod a non-null {@link java.lang.reflect.Method} representing listener's method
     * @param handledEventClass a non-null {@link java.lang.Class} representing handled event's class
     */
    public ListenerMetadata(Class<?> listenerClass, Method listenerMethod, Class<?> handledEventClass) {
        this.listenerClass = listenerClass;
        this.listenerMethod = listenerMethod;
        this.handledEventClass = handledEventClass;
    }

    /**
     * Get listener's {@link java.lang.Class}
     * @return a non-null class
     */
    public Class<?> getListenerClass() {
        return listenerClass;
    }

    /**
     * Get listener's {@link java.lang.reflect.Method}
     * @return a non-null method
     */
    public Method getListenerMethod() {
        return listenerMethod;
    }

    /**
     * Get handled event's {@link java.lang.Class}
     * @return a non-null class
     */
    public Class<?> getHandledEventClass() {
        return handledEventClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListenerMetadata metadata = (ListenerMetadata) o;

        return handledEventClass.equals(metadata.handledEventClass) &&
               listenerClass.equals(metadata.listenerClass) &&
               listenerMethod.equals(metadata.listenerMethod);

    }

    @Override
    public int hashCode() {
        int result = listenerClass.hashCode();
        result = 31 * result + listenerMethod.hashCode();
        result = 31 * result + handledEventClass.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ListenerMetadata(" +
                "listenerClass=" + listenerClass +
                ", listenerMethod=" + listenerMethod +
                ", handledEventClass=" + handledEventClass +
                ')';
    }
}
