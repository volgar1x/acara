package com.github.blackrush.acara;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * {@code ClassListenerMetadataLookup} lookup {@link com.github.blackrush.acara.ListenerMetadata} by listener's {@link java.lang.Class}
 * It also provide cache the result.
 */
public abstract class ClassListenerMetadataLookup implements ListenerMetadataLookup {

    private final Map<Class<?>, ImmutableList<ListenerMetadata>> cache
            = new ConcurrentHashMap<>();

    protected abstract Stream<ListenerMetadata> lookupClass(Class<?> klass);

    @Override
    public final Stream<ListenerMetadata> lookup(Object listener) {
        return cache.computeIfAbsent(listener.getClass(), this::compute).stream();
    }

    private ImmutableList<ListenerMetadata> compute(Class<?> klass) {
        return lookupClass(klass).collect(collector());
    }

    private static final Collector<Object, ImmutableList.Builder<Object>, ImmutableList<Object>> COLLECTOR
            = new Collector<Object, ImmutableList.Builder<Object>, ImmutableList<Object>>() {
        @Override
        public Supplier<ImmutableList.Builder<Object>> supplier() {
            return ImmutableList::builder;
        }

        @Override
        public BiConsumer<ImmutableList.Builder<Object>, Object> accumulator() {
            return ImmutableList.Builder::add;
        }

        @Override
        public BinaryOperator<ImmutableList.Builder<Object>> combiner() {
            return (a, b) -> { a.addAll(b.build()); return a; };
        }

        @Override
        public Function<ImmutableList.Builder<Object>, ImmutableList<Object>> finisher() {
            return ImmutableList.Builder::build;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return ImmutableSet.of();
        }
    };

    @SuppressWarnings("unchecked")
    private static <T> Collector<T, ?, ImmutableList<T>> collector() { return (Collector) COLLECTOR; }
}
