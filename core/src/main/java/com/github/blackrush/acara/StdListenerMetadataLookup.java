package com.github.blackrush.acara;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@inheritDoc}
 * Only lookup for {@link com.github.blackrush.acara.Listener} listeners.
 * @see com.github.blackrush.acara.Listener
 */
public class StdListenerMetadataLookup implements ListenerMetadataLookup {
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

    Stream<Class<?>> traverseInheritance(Class<?> klass) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<Class<?>>(Long.MAX_VALUE, 0) {
            Class<?> cur = klass;

            @Override
            public boolean tryAdvance(Consumer<? super Class<?>> action) {
                action.accept(cur);

                Class<?> parent = cur.getSuperclass();
                if (parent == Object.class) {
                    return false;
                } else {
                    cur = parent;
                    return true;
                }
            }
        }, false);
    }

    static Stream<Method> methodStream(Class<?> klass) {
        return Stream.of(klass.getDeclaredMethods());
    }

    static boolean isListener(Method method) {
        Listener ann = method.getAnnotation(Listener.class);
        return ann != null && !ann.disabled();
    }

    static boolean isValidListener(Method method) {
        return method.getParameterCount() != 1;
    }

    boolean isValidListenerOrWarn(Method method) {
        if (isValidListener(method)) {
            log.warn("method {} has an invalid signature", method);
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<ListenerMetadata> lookup(Object listener) {
        Class<?> listenerClass = listener.getClass();
        
        return traverseInheritance(listenerClass)
                .flatMap(StdListenerMetadataLookup::methodStream)
                .filter(StdListenerMetadataLookup::isListener)
                .filter(this::isValidListenerOrWarn)
                .map(method -> new ListenerMetadata(listenerClass, method, method.getParameterTypes()[0]))
                ;
    }
}
