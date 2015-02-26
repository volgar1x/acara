package com.github.blackrush.acara;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

final class EventBusImpl implements EventBus {
    final EventMetadataBuilder eventMetadataBuilder;
    final ListenerBuilder listenerBuilder;
    final Worker worker;

    /* state => (signature => behavior)
             ^             ^
             |             |
           rw m/t         ro            */
    private final ConcurrentMap<Object, Map<EventMetadata, Listener>> listeners
            = new ConcurrentHashMap<>();

    EventBusImpl(EventMetadataBuilder eventMetadataBuilder, ListenerBuilder listenerBuilder, Worker worker) {
        this.eventMetadataBuilder = eventMetadataBuilder;
        this.listenerBuilder = listenerBuilder;
        this.worker = worker;
    }

    private Listener lookupListener(EventMetadata meta, Map<EventMetadata, Listener> listeners) {
        Listener listener = null;
        for (EventMetadata cur = meta; listener == null && cur != null; cur = cur.getParent()) {
            listener = listeners.get(cur);
        }
        return listener;
    }

    private Map<EventMetadata, Listener> buildListeners(Object sub) {
        return listenerBuilder.build(sub)
                .collect(Collectors.toMap(
                        Listener::getHandledEvent,
                        Function.identity()));
    }

    @Override
    public Future<List<Object>> publish(Object event) {
        EventMetadata meta = eventMetadataBuilder.build(event);

        // there are, at most, as many responses as there are subscribers
        // in reality, we hardly ever get as many responses
        List<Future<Object>> results = new LinkedList<>();

        listeners.forEach((sub, listeners) -> {
            Listener listener = lookupListener(meta, listeners);
            if (listener != null) {
                Future<Object> result = listener.dispatch(sub, event, worker);
                results.add(result);
            }
        });

        return Futures.collect(results);
    }

    @Override
    public Subscription subscribe(Object sub) {
        listeners.putIfAbsent(sub, buildListeners(sub));
        return () -> listeners.remove(sub);
    }

    @Override
    public Subscription subscribeMany(Collection<?> subs) {
        for (Object sub : subs) {
            listeners.putIfAbsent(sub, buildListeners(sub));
        }
        return () -> subs.forEach(listeners::remove);
    }
}
