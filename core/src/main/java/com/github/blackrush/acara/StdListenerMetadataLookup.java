package com.github.blackrush.acara;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StdListenerMetadataLookup implements ListenerMetadataLookup {
    public static final StdListenerMetadataLookup SHARED = new StdListenerMetadataLookup(
            LoggerFactory.getLogger(StdListenerMetadataLookup.class));

    private final Logger log;

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

    Stream<Method> methodStream(Class<?> klass) {
        return Stream.of(klass.getDeclaredMethods());
    }

    boolean isListener(Method method) {
        Listener ann = method.getAnnotation(Listener.class);
        return ann != null && !ann.disabled();
    }

    boolean isValidListener(Method method) {
        if (method.getParameterCount() != 1) {
            log.warn("method {} has an invalid signature", method);
            return false;
        }

        return true;
    }

    @Override
    public Stream<ListenerMetadata> lookup(Object listener) {
        Class<?> listenerClass = listener.getClass();
        
        return traverseInheritance(listenerClass)
                .flatMap(this::methodStream)
                .filter(this::isListener)
                .filter(this::isValidListener)
                .map(method -> new ListenerMetadata(listenerClass, method, method.getParameterTypes()[0]))
                ;
    }
}
