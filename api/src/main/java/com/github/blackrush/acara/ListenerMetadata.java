package com.github.blackrush.acara;

import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

/**
 * {@link com.github.blackrush.acara.ListenerMetadata} holds all useful listener metadata.
 *
 * {@link com.github.blackrush.acara.ListenerMetadata} is a pure immutable type, implementing equals/hashCode.
 */
public final class ListenerMetadata {
    private final Class<?> listenerClass;
    private final Method listenerMethod;
    private final EventMetadata handledEventMetadata;

    /**
     * Default constructor.
     * @param listenerClass a non-null {@link java.lang.Class} representing listener's class
     * @param listenerMethod a non-null {@link java.lang.reflect.Method} representing listener's method
     * @param handledEventMetadata a non-null {@link com.github.blackrush.acara.EventMetadata} representing handled event's metadata
     */
    public ListenerMetadata(Class<?> listenerClass, Method listenerMethod, EventMetadata handledEventMetadata) {
        this.listenerClass = requireNonNull(listenerClass, "listenerClass");
        this.listenerMethod = requireNonNull(listenerMethod, "listenerMethod");
        this.handledEventMetadata = requireNonNull(handledEventMetadata, "handledEventMetadata");
    }

    /**
     * Get listener's {@link java.lang.Class}
     * @return a non-null class
     */
    public Class<?> getListenerClass() {
        return listenerClass;
    }

    /**
     * Create a new {@link com.github.blackrush.acara.ListenerMetadata} with a new listener class
     * @param listenerClass a non-null {@link java.lang.Class} representing listener's class
     * @return a non-null {@link com.github.blackrush.acara.ListenerMetadata} or the same instance if the parameter is equal to actual listener class
     */
    public ListenerMetadata withListenerClass(Class<?> listenerClass) {
        return this.listenerClass != listenerClass ? this : new ListenerMetadata(listenerClass, listenerMethod, handledEventMetadata);
    }

    /**
     * Get listener's {@link java.lang.reflect.Method}
     * @return a non-null method
     */
    public Method getListenerMethod() {
        return listenerMethod;
    }

    /**
     * Create a new {@link com.github.blackrush.acara.ListenerMetadata} with a new listener method
     * @param listenerMethod a non-null {@link java.lang.Class} representing listener's method
     * @return a non-null {@link com.github.blackrush.acara.ListenerMetadata} or the same instance if the parameter is equal to actual listener method
     */
    public ListenerMetadata withListenerMethod(Method listenerMethod) {
        return this.listenerMethod != listenerMethod ? this : new ListenerMetadata(listenerClass, listenerMethod, handledEventMetadata);
    }

    /**
     * Get handled event's {@link java.lang.Class}
     * @return a non-null value
     */
    public EventMetadata getHandledEventMetadata() {
        return handledEventMetadata;
    }

    /**
     * Create a new {@link com.github.blackrush.acara.ListenerMetadata} with a new handled event metadata
     * @param handledEventMetadata a non-null {@link com.github.blackrush.acara.EventMetadata} representing handled event's metadata
     * @return a non-null {@link com.github.blackrush.acara.ListenerMetadata} or the same instance if the parameter is equal to actual handled event metadata
     */
    public ListenerMetadata withHandledEventMetadata(EventMetadata handledEventMetadata) {
        return this.handledEventMetadata != handledEventMetadata ? this : new ListenerMetadata(listenerClass, listenerMethod, handledEventMetadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListenerMetadata metadata = (ListenerMetadata) o;

        return handledEventMetadata.equals(metadata.handledEventMetadata) &&
               listenerClass.equals(metadata.listenerClass) &&
               listenerMethod.equals(metadata.listenerMethod);

    }

    @Override
    public int hashCode() {
        int result = listenerClass.hashCode();
        result = 31 * result + listenerMethod.hashCode();
        result = 31 * result + handledEventMetadata.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ListenerMetadata(" +
                "listenerClass=" + listenerClass +
                ", listenerMethod=" + listenerMethod +
                ", handledEventMetadata=" + handledEventMetadata +
                ')';
    }
}
