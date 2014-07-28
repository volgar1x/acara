package com.github.blackrush.acara;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class StreamUtils {
    private StreamUtils() {}

    public static <T> Iterable<T> asIterable(Stream<T> stream) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Spliterators.iterator(stream.spliterator());
            }

            @Override
            public void forEach(Consumer<? super T> action) {
                stream.forEach(action);
            }

            @Override
            public Spliterator<T> spliterator() {
                return stream.spliterator();
            }
        };
    }

    public static <T> Stream<T> asStream(Optional<T> opt) {
        return opt.map(Stream::of).orElse(Stream.empty());
    }

    public static <T> Stream<T> iterate(Optional<T> seed, Function<T, Optional<T>> fn) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
            Optional<T> cur = seed;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                cur.ifPresent(action);
                cur = cur.flatMap(fn);
                return cur.isPresent();
            }
        }, false);
    }

    public static <T> Stream<T> collect(Stream<T> seed, Function<T, Stream<T>> fn) {
        return seed.<T>flatMap(it -> Stream.concat(Stream.of(it), collect(fn.apply(it), fn)));
    }

    public static Stream<Class<?>> traverseInheritance(Class<?> klass) {
        return traverseInheritance(klass, Object.class);
    }

    public static Stream<Class<?>> traverseInheritance(Class<?> klass, Class<?> limit) {
        return klass == limit ? Stream.empty() : iterate(Optional.of(klass), it -> optionalParent(it, limit));
    }

    public static Optional<Class<?>> optionalParent(Class<?> klass, Class<?> limit) {
        return klass.getSuperclass() == limit
                ? Optional.empty()
                : Optional.of(klass.getSuperclass())
                ;
    }

    public static Stream<Class<?>> directParent(Class<?> klass, Class<?> limit) {
        Stream<Class<?>> ifaces = Stream.of(klass.getInterfaces());

        Class<?> superclass = klass.getSuperclass();
        if (superclass == null || superclass == limit) {
            return ifaces;
        }

        return Stream.concat(Stream.of(superclass), ifaces);
    }

    public static <T> Stream<T> times(int n, Supplier<T> fn) {
        return IntStream.range(0, n).mapToObj(i -> fn.get());
    }
}
