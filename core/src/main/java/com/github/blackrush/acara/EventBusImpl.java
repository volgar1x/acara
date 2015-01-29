package com.github.blackrush.acara;

import org.fungsi.concurrent.Future;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.StampedLock;

final class EventBusImpl implements EventBus {
    final EventMetadataBuilder eventMetadataBuilder;
    final ListenerBuilder listenerBuilder;

    private class Listeners {
        Deque<Listener> list = new ConcurrentLinkedDeque<>();
        StampedLock lock = new StampedLock();
    }
    private final Map<EventMetadata, Listeners> listeners = new ConcurrentHashMap<>();

    EventBusImpl(EventMetadataBuilder eventMetadataBuilder, ListenerBuilder listenerBuilder) {
        this.eventMetadataBuilder = eventMetadataBuilder;
        this.listenerBuilder = listenerBuilder;
    }

    private Listeners subscribe(Listener listener, EventMetadata evt) {
        Listeners l = listeners.computeIfAbsent(evt, x -> new Listeners());
        long stamp = l.lock.writeLock();
        try {
            l.list.add(listener);
            return l;
        } finally {
            l.lock.unlockWrite(stamp);
        }
    }

    @Override
    public Subscription subscribe(Object subscriber) {
        Listener listener = listenerBuilder.build(subscriber);

        Listeners l = subscribe(listener, listener.getHandledEvent());

        return () -> {
            long stamp = l.lock.writeLock();
            try {
                l.list.remove(listener);
            } finally {
                l.lock.unlockWrite(stamp);
            }
        };
    }

    @Override
    public Subscription subscribeMany(Collection<?> subscribers) {
        for (Object subscriber : subscribers) {
            Listener listener = listenerBuilder.build(subscriber);

        }
    }

    @Override
    public Future<List<Object>> publish(Object event) {
        return null;
    }
}
