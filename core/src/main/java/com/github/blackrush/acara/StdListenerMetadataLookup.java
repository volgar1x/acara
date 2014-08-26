package com.github.blackrush.acara;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * {@inheritDoc}
 * Only lookup for {@link com.github.blackrush.acara.Listener} listeners.
 * @see com.github.blackrush.acara.Listener
 */
public final class StdListenerMetadataLookup extends ClassListenerMetadataLookup {
    /**
     * A shareable {@link com.github.blackrush.acara.StdListenerMetadataLookup} instance
     */
    public static final StdListenerMetadataLookup SHARED = new StdListenerMetadataLookup(
            LoggerFactory.getLogger(StdListenerMetadataLookup.class));

    private final Logger log;

    /**
     * Default constructor.
     * @param log a non-null {@link org.slf4j.Logger} used to warn in case of invalid {@link com.github.blackrush.acara.Listener}
     */
    public StdListenerMetadataLookup(Logger log) {
        this.log = log;
    }

    static Stream<Method> methodStream(Class<?> klass) {
        return Stream.of(klass.getDeclaredMethods());
    }

    private static boolean isListener(Method method) {
        Listener ann = method.getAnnotation(Listener.class);
        return ann != null && !ann.disabled();
    }

    private static boolean listenerHasValidSignature(Method method) {
        return method.getParameterCount() == 1;
    }

    static boolean isValidListener(Method method) {
        return isListener(method) && listenerHasValidSignature(method);
    }

    boolean isValidListenerOrWarn(Method method) {
        if (!isListener(method)) {
            return false;
        }

        if (!listenerHasValidSignature(method)) {
            log.warn("method {} has an invalid signature", method);
            return false;
        }

        return true;
    }

    EventMetadata buildEventMetadata(Method method) {
        return new StdEventMetadata(method.getParameterTypes()[0]);
    }

    @Override
    protected Stream<ListenerMetadata> lookupClass(Class<?> klass) {
        return StreamUtils.traverseInheritance(klass)
                .flatMap(StdListenerMetadataLookup::methodStream)
                .filter(this::isValidListenerOrWarn)
                .map(method -> new ListenerMetadata(klass, method, buildEventMetadata(method)))
                ;
    }
}
