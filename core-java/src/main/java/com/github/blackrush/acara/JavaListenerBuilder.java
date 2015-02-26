package com.github.blackrush.acara;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class JavaListenerBuilder implements ListenerBuilder {
    @Override
    public final Stream<Listener> build(Object o) {
        List<Method> methods = new LinkedList<>();
        Class<?> klass = o.getClass();
        while (klass != Object.class) {
            Collections.addAll(methods, klass.getDeclaredMethods());
            klass = klass.getSuperclass();
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
