package com.github.blackrush.acara;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaListenerBuilder implements ListenerBuilder {
    private Map<Class<?>, Collection<Listener>> cache = new ConcurrentHashMap<>();

    @Override
    public final Stream<Listener> build(Object o) {
        Class<?> klass = o.getClass();
        return cache.computeIfAbsent(klass, this::cache).stream();
    }

    private Collection<Listener> cache(Class<?> klass) {
        return scanAll(klass).collect(Collectors.toList());
    }

    private Stream<Listener> scanAll(Class<?> klass) {
        List<Method> methods = new LinkedList<>();
        for (Class<?> it = klass; it != Object.class; it = it.getSuperclass()) {
            Collections.addAll(methods, it.getDeclaredMethods());
        }

        return methods.stream().flatMap(this::scan);
    }

    protected Stream<Listener> scan(Method method) {
        if (!method.isAnnotationPresent(Listen.class)) {
            return Stream.empty();
        }
        if (method.getParameterCount() != 1) {
            // TODO warn user
            return Stream.empty();
        }

        JavaEventMetadata<?> meta = new JavaEventMetadata<>(method.getParameterTypes()[0]);
        JavaListener<?> listener = new JavaListener<>(meta, method);
        return Stream.of(listener);
    }
}
