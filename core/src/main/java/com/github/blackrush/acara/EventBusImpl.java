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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
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

    final Worker                 worker;
    final boolean                defaultAsync;
    final ListenerMetadataLookup metadataLookup;
    final DispatcherLookup       dispatcherLookup;
    final Supervisor             supervisor;
    final EventMetadataLookup    eventMetadataLookup;
    final Logger                 logger;

    final ListMultimap<EventMetadata, Listener> listeners = LinkedListMultimap.create();
    final StampedLock lock = new StampedLock();

    EventBusImpl(Worker worker, boolean defaultAsync, ListenerMetadataLookup metadataLookup, DispatcherLookup dispatcherLookup, Supervisor supervisor, EventMetadataLookup eventMetadataLookup, Logger logger) {
        this.worker              = requireNonNull(worker, "worker");
        this.defaultAsync        = defaultAsync;
        this.metadataLookup      = requireNonNull(metadataLookup, "metadataLookup");
        this.dispatcherLookup    = requireNonNull(dispatcherLookup, "dispatcherLookup");
        this.supervisor          = requireNonNull(supervisor, "supervisor");
        this.eventMetadataLookup = requireNonNull(eventMetadataLookup, "eventMetadataLookup");
        this.logger              = requireNonNull(logger, "logger");
    }

    static Stream<Either<Object, Throwable>> dispatch(Stream<Listener> listeners, Object event) {
        return listeners.map(listener -> {
            Either<Object, Throwable> answer = listener.dispatcher.dispatch(listener.instance, event);

            if (listener.metadata.getListenerMethod().getReturnType() == void.class) {
                return answer.leftMap(x -> unit());
            }

            return answer;
        });
    }

    static Stream<EventMetadata> resolveHierarchy(EventMetadata lowestEvent) {
        return StreamUtils.collect(Stream.of(lowestEvent), EventMetadata::getParent).distinct();
    }

    List<Object> supervise(Stream<Either<Object, Throwable>> stream, List<Throwable> toDispatch) {
        List<Either<Object, Throwable>> unsupervised = stream.collect(Collectors.toList());
        List<Object> supervised = new ArrayList<>(unsupervised.size());

        loop:
        for (Either<Object, Throwable> e : unsupervised) {
            if (e.isLeft()) {
                if (e.left() != unit()) {
                    supervised.add(e.left());
                }
            } else {
                Throwable cause = e.right();
                switch (supervisor.handle(cause)) {
                    case ESCALATE:
                        throw Throwables.propagate(cause);

                    case STOP:
                        logger.warn("uncaught exception", cause);
                        break loop;

                    case IGNORE:
                        logger.warn("uncaught exception", cause);
                        break;

                    case NEW_EVENT:
                        toDispatch.add(cause);
                        break;

                    default:
                        throw new Error();
                }
            }
        }

        return supervised;
    }

    List<Object> doDispatch(Object event, Collection<Listener> listeners, boolean async) {
        Stream<Either<Object, Throwable>> answers = dispatch(listeners.stream(), event);

        List<Throwable> toDispatch = new ArrayList<>();
        List<Object> supervised = supervise(answers, toDispatch);

        toDispatch.stream().map(cause -> new SupervisedEvent(event, cause))
                .forEach(async ? this::publishAsync : this::publishSync);

        return supervised;
    }

    Collection<Listener> readListeners(Stream<EventMetadata> meta) {
        return meta.flatMap(it -> listeners.get(it).stream()).collect(Collectors.toList());
    }

    Collection<Listener> getListeners(Object event) {
        EventMetadata meta = eventMetadataLookup.lookup(event)
                .orElseThrow(() -> new IllegalStateException("couldn't lookup metadata for event " + event))
                ;

        List<EventMetadata> parents = resolveHierarchy(meta).collect(Collectors.toList());
        if (parents.isEmpty()) return ImmutableList.of();

        long stamp = lock.tryOptimisticRead();
        Collection<Listener> res = readListeners(parents.stream());
        if (lock.validate(stamp)) {
            return res;
        }

        stamp = lock.readLock();
        try {
            return readListeners(parents.stream());
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public Future<List<Object>> publishAsync(Object event) {
        Collection<Listener> listeners = getListeners(event);
        return worker.submit(() -> doDispatch(event, listeners, true));
    }

    @Override
    public List<Object> publishSync(Object event) {
        Collection<Listener> listeners = getListeners(event);
        return doDispatch(event, listeners, false);
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

    @Override
    public EventBus subscribe(Object subscriber) {
        List<Listener> listeners = metadataLookup.lookup(subscriber)
                .flatMap(m -> asStream(dispatcherLookup.lookup(m))
                        .map(d -> new Listener(m, d, subscriber)))
                .collect(Collectors.toList())
                ;

        if (listeners.isEmpty()) return this;

        long stamp = lock.writeLock();
        try {
            for (Listener listener : listeners) {
                this.listeners.put(listener.metadata.getHandledEventMetadata(), listener);
            }

            return this;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void unsubscribe(Object subscriber) {
        long stamp = lock.readLock();
        try {
            boolean remove = false;

            Iterator<Listener> it = listeners.values().iterator();
            while (it.hasNext()) {
                Listener listener = it.next();

                if (listener.instance == subscriber) {
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
}
