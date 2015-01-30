package com.github.blackrush.acara;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class EventBusImpl implements EventBus {
    final EventMetadataBuilder eventMetadataBuilder;
    final ListenerBuilder listenerBuilder;
    final Worker worker;

    private final Map<EventMetadata, Deque<Listener>> listeners
            = new ConcurrentHashMap<>();

    EventBusImpl(EventMetadataBuilder eventMetadataBuilder, ListenerBuilder listenerBuilder, Worker worker) {
        this.eventMetadataBuilder = eventMetadataBuilder;
        this.listenerBuilder = listenerBuilder;
        this.worker = worker;
    }

    @Override
    public Future<List<Object>> publish(Object event) {
        EventMetadata metadata = eventMetadataBuilder.build(event);
        Deque<Listener> deq = listeners.get(metadata);
        return deq.stream()
                .map(x -> x.dispatch(event, worker))
                .collect(Futures.collect());
    }

    Subscription subscribe(Stream<Listener> stream) {
        Map<EventMetadata, List<Listener>> l = stream.collect(
            Collectors.groupingBy(Listener::getHandledEvent));

        l.forEach((metadata, list) -> {
            Deque<Listener> deq = listeners.computeIfAbsent(metadata, x -> new ConcurrentLinkedDeque<>());
            deq.addAll(list);
        });

        return () ->
            l.forEach((metadata, list) ->
                listeners.get(metadata).removeAll(list)
            );
    }

    @Override
    public Subscription subscribe(Object sub) {
        return subscribe(listenerBuilder.build(sub));
    }

    @Override
    public Subscription subscribeMany(Collection<?> subs) {
        return subscribe(subs.stream().flatMap(listenerBuilder::build));
    }
}
