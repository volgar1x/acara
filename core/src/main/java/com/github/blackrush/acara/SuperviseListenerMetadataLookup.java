package com.github.blackrush.acara;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * {@inheritDoc}
 */
public final class SuperviseListenerMetadataLookup implements ListenerMetadataLookup {

    public static final SuperviseListenerMetadataLookup SHARED = new SuperviseListenerMetadataLookup();

    public static boolean isSuperviseListener(Method method) {
        return method.isAnnotationPresent(Supervise.class);
    }

    public static boolean isValidSuperviseListener(Method method) {
        return method.getParameterCount() == 2 &&
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
    private Class<Throwable> getHandledCauseClass(Method method) {
        return (Class) method.getParameterTypes()[0];
    }

    private Class<?> getHandledInitialEventClass(Method method) {
        return method.getParameterTypes()[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<ListenerMetadata> lookup(Object listener) {
        return StreamUtils.traverseInheritance(listener.getClass())
                .flatMap(it -> Stream.of(it.getDeclaredMethods()))
                .filter(this::checkSuperviseListenerValidity)
                .map(m -> new ListenerMetadata(listener.getClass(), m, new SupervisedEventMetadata(
                        getHandledCauseClass(m),
                        getHandledInitialEventClass(m)
                )));
    }
}
