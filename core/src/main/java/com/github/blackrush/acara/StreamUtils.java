package com.github.blackrush.acara;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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

    public static Stream<Class<?>> traverseInheritance(Class<?> klass) {
        if (klass == Object.class) {
            return Stream.empty();
        }

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

    public static Stream<Class<?>> directParent(Class<?> klass, Class<?> limit) {
        Class<?> superclass = klass.getSuperclass();
        if (superclass == limit) {
            return Stream.empty();
        }
        return Stream.of(superclass);
    }

    public static <T, U, R> Stream<R> combination(T a, U b, Function<T, Stream<T>> fnR, Function<U, Stream<U>> fnU, BiFunction<T, U, R> fnT) {
        Stream<T> aa = fnR.apply(a);
        Stream<U> bb = fnU.apply(b);

        return Stream.<Stream<R>>of(
                aa.map(x -> fnT.apply(x, b)),
                bb.map(x -> fnT.apply(a, x)),
                aa.flatMap(xA -> bb.map(xB -> fnT.apply(xA, xB)))
        ).reduce(Stream.empty(), Stream::concat);
    }
}
