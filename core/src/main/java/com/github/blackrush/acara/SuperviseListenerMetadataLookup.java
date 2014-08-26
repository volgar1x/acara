package com.github.blackrush.acara;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@inheritDoc}
 */
public final class SuperviseListenerMetadataLookup extends ClassListenerMetadataLookup {

    public static SuperviseListenerMetadataLookup newDefault() {
        return new SuperviseListenerMetadataLookup();
    }

    /**
     * Test if given {@link java.lang.reflect.Method} is a {@link com.github.blackrush.acara.Supervise} listener.
     * This method does not check his validity.
     *
     * @param method a non-null method
     * @return {@literal true} if given {@link java.lang.reflect.Method} is a {@link com.github.blackrush.acara.Supervise} listener, {@literal false} otherwise
     * @see #isValidSuperviseListener(java.lang.reflect.Method)
     */
    public static boolean isSuperviseListener(Method method) {
        return method.isAnnotationPresent(Supervise.class);
    }

    /**
     * Test if given {@link java.lang.reflect.Method} is a valid {@link com.github.blackrush.acara.Supervise} listener.
     * This method does not check if given {@link java.lang.reflect.Method} is actually a {@link com.github.blackrush.acara.Supervise} listener, only his validity.
     *
     * @param method a non-null method
     * @return {@literal true} if given {@link java.lang.reflect.Method} is a valid {@link com.github.blackrush.acara.Supervise} listener, {@literal false} otherwise
     * @see #isSuperviseListener(java.lang.reflect.Method)
     */
    public static boolean isValidSuperviseListener(Method method) {
        return (method.getParameterCount() == 1 || method.getParameterCount() == 2) &&
               Throwable.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    private static final Logger log = LoggerFactory.getLogger(SuperviseListenerMetadataLookup.class);

    private boolean checkSuperviseListenerValidity(Method method) {
        if (!isSuperviseListener(method)) {
            return false;
        }
        if (!isValidSuperviseListener(method)) {
            log.warn("method {} has an invalid signature");
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static Class<?> getHandledCauseClass(Method method) {
        return method.getParameterTypes()[0];
    }

    private static Optional<Class<?>> getHandledInitialEventClass(Method method) {
        return method.getParameterCount() == 2
                ? Optional.of(method.getParameterTypes()[1])
                : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<ListenerMetadata> lookupClass(Class<?> klass) {
        return StreamUtils.traverseInheritance(klass)
                .flatMap(it -> Stream.of(it.getDeclaredMethods()))
                .filter(this::checkSuperviseListenerValidity)
                .map(m -> new ListenerMetadata(klass, m, new SupervisedEventMetadata(
                        getHandledCauseClass(m),
                        getHandledInitialEventClass(m).orElse(Object.class)
                )));
    }
}
