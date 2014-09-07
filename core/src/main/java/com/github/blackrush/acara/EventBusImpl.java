package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.Dispatcher;
import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;
import com.github.blackrush.acara.supervisor.event.SupervisedEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import org.fungsi.Either;
import org.fungsi.Throwables;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.blackrush.acara.StreamUtils.asStream;
import static java.util.Objects.requireNonNull;
import static org.fungsi.Unit.unit;

final class EventBusImpl implements EventBus {
    class Listener {
        final ListenerMetadata metadata;
        final Dispatcher dispatcher;
        final Object instance;

        Listener(ListenerMetadata metadata, Dispatcher dispatcher, Object instance) {
            this.metadata = metadata;
            this.dispatcher = dispatcher;
            this.instance = instance;
        }
    }

    class EventWithListeners {
        final Object event;
        final Collection<Listener> listeners;

        EventWithListeners(Object event, Collection<Listener> listeners) {
            this.event = event;
            this.listeners = listeners;
        }
    }

    private final Worker                 worker;
    private final boolean                defaultAsync;
    private final ListenerMetadataLookup metadataLookup;
    private final DispatcherLookup       dispatcherLookup;
    private final Supervisor             supervisor;
    private final EventMetadataLookup    eventMetadataLookup;
    private final Logger                 logger;

    final ListMultimap<EventMetadata, Listener> listeners = LinkedListMultimap.create();
    private final StampedLock lock = new StampedLock();

    EventBusImpl(Worker worker, boolean defaultAsync, ListenerMetadataLookup metadataLookup, DispatcherLookup dispatcherLookup, Supervisor supervisor, EventMetadataLookup eventMetadataLookup, Logger logger) {
        this.worker              = requireNonNull(worker, "worker");
        this.defaultAsync        = defaultAsync;
        this.metadataLookup      = requireNonNull(metadataLookup, "metadataLookup");
        this.dispatcherLookup    = requireNonNull(dispatcherLookup, "dispatcherLookup");
        this.supervisor          = requireNonNull(supervisor, "supervisor");
        this.eventMetadataLookup = requireNonNull(eventMetadataLookup, "eventMetadataLookup");
        this.logger              = requireNonNull(logger, "logger");
    }

    private static Stream<EventMetadata> resolveHierarchy(EventMetadata lowestEvent) {
        return StreamUtils.collect(Stream.of(lowestEvent), EventMetadata::getParent).distinct();
    }

    @SuppressWarnings("unchecked")
    Future<List<Object>> doDispatch(Object event, Collection<Listener> listeners, boolean supervise) {
        List<Object> immediate = new LinkedList<>();
        List<Future<Object>> futures = new LinkedList<>();
        List<SupervisedEvent> supervisedEvents = new LinkedList<>();

        for (Listener listener : listeners) {
            Either<Object, Throwable> result = listener.dispatcher.dispatch(listener.instance, event);

            if (result.isLeft()) {
                Object o = result.left();
                if (o != null && o != unit()) {
                    if (o instanceof Future) {
                        futures.add((Future<Object>) o);
                    } else {
                        immediate.add(o);
                    }
                }
            } else {
                Throwable failure = result.right();
                if (!supervise) {
                    throw Throwables.propagate(failure);
                }

                switch (supervisor.handle(failure)) {
                    case ESCALATE:
                        throw Throwables.propagate(failure);

                    case IGNORE:
                        logger.warn("uncaught exception", failure);
                        break;

                    case NEW_EVENT:
                        supervisedEvents.add(new SupervisedEvent(event, failure));
                        break;
                }
            }
        }

        getMultipleListeners(supervisedEvents).forEach(x -> doDispatch(x.event, x.listeners, false));

        return Futures.collect(futures).map(results -> {
            immediate.addAll(results);
            return immediate;
        });
    }

    Stream<Listener> readListeners(Stream<EventMetadata> meta) {
        return meta.flatMap(it -> listeners.get(it).stream());
    }

    List<Listener> getListeners(Object event) {
        EventMetadata meta = eventMetadataLookup.lookup(event)
                .orElseThrow(() -> new IllegalStateException("couldn't lookup metadata for event " + event))
                ;

        List<EventMetadata> parents = resolveHierarchy(meta).collect(Collectors.toList());
        if (parents.isEmpty()) return ImmutableList.of();

        long stamp = lock.tryOptimisticRead();
        List<Listener> res = readListeners(parents.stream()).collect(Collectors.toList());
        if (lock.validate(stamp)) {
            return res;
        }

        stamp = lock.readLock();
        try {
            return readListeners(parents.stream()).collect(Collectors.toList());
        } finally {
            lock.unlockRead(stamp);
        }
    }

    List<EventWithListeners> getMultipleListeners(List<?> events) {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        long stamp = lock.readLock();
        try {
            List<EventWithListeners> result = new LinkedList<>();
            for (Object event : events) {
                List<Listener> listeners =
                    resolveHierarchy(eventMetadataLookup.lookup(event).get())
                        .flatMap(meta -> this.listeners.get(meta).stream())
                        .collect(Collectors.toList());

                if (!listeners.isEmpty()) {
                    result.add(new EventWithListeners(event, listeners));
                }
            }
            return result;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public Future<List<Object>> publishAsync(Object event) {
        Collection<Listener> listeners = getListeners(event);
        return worker.execute(() -> doDispatch(event, listeners, true));
    }

    @Override
    public List<Object> publishSync(Object event) {
        Collection<Listener> listeners = getListeners(event);
        return doDispatch(event, listeners, true).get();
    }

    @Override
    public Future<List<Object>> publish(Object event) {
        if (defaultAsync) {
            return publishAsync(event);
        } else {
            try {
                return Futures.success(publishSync(event));
            } catch (Throwable cause) {
                return Futures.failure(cause); // see SupervisorDirective.ESCALATE
            }
        }
    }

    private void addListeners(List<Listener> listeners) {
        long stamp = lock.writeLock();
        try {
            for (Listener listener : listeners) {
                this.listeners.put(listener.metadata.getHandledEventMetadata(), listener);
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private void removeListeners(Predicate<Listener> fn) {
        long stamp = lock.readLock();
        try {
            boolean remove = false;

            Iterator<Listener> it = listeners.values().iterator();
            while (it.hasNext()) {
                Listener listener = it.next();

                if (fn.test(listener)) {
                    if (!remove) {
                        long writeLock = lock.tryConvertToWriteLock(stamp);
                        if (writeLock != 0L) {
                            stamp = writeLock;
                        } else {
                            lock.unlockRead(stamp);
                            stamp = lock.writeLock();
                        }
                    }

                    it.remove();
                    remove = true;
                }
            }
        } finally {
            lock.unlock(stamp);
        }
    }

    private Stream<Listener> createListeners(Object subscriber) {
        return metadataLookup.lookup(subscriber)
                .flatMap(m -> asStream(dispatcherLookup.lookup(m))
                        .map(d -> new Listener(m, d, subscriber)))
                ;
    }

    @Override
    public EventBus subscribe(Object subscriber) {
        List<Listener> listeners = createListeners(subscriber).collect(Collectors.toList());

        if (!listeners.isEmpty()) {
            addListeners(listeners);
        }

        return this;
    }

    @Override
    public EventBus subscribeMany(Collection<?> subscribers) {
        List<Listener> listeners = subscribers.stream().flatMap(this::createListeners).collect(Collectors.toList());

        if (!listeners.isEmpty()) {
            addListeners(listeners);
        }

        return this;
    }

    @Override
    public void unsubscribe(Object subscriber) {
        removeListeners(it -> it.instance == subscriber);
    }

    @Override
    public void unsubscribeMany(Collection<?> listeners) {
        removeListeners(it -> listeners.contains(it.instance));
    }
}
