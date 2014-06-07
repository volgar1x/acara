package com.github.blackrush.acara;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
}
