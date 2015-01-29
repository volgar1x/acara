package com.github.blackrush.acara;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class StreamUtils {
    private StreamUtils() {}

    static <T> Stream<T> untilNull(T seed, UnaryOperator<T> fn) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
            T cur = seed;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                if (cur == null) {
                    return false;
                }
                action.accept(cur);
                cur = fn.apply(cur);
                return cur != null;
            }
        }, false);
    }
}
