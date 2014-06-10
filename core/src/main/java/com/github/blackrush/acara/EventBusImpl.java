package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.Dispatcher;
import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;
import com.github.blackrush.acara.supervisor.event.SupervisedEvent;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import org.fungsi.Either;
import org.fungsi.Throwables;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

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

    final Map<ListenerMetadata, Dispatcher>     dispatchers = new HashMap<>();
    final ListMultimap<EventMetadata, Listener> listeners   = LinkedListMultimap.create();

    EventBusImpl(Worker worker, boolean defaultAsync, ListenerMetadataLookup metadataLookup, DispatcherLookup dispatcherLookup, Supervisor supervisor, EventMetadataLookup eventMetadataLookup, Logger logger) {
        this.worker              = requireNonNull(worker, "worker");
        this.defaultAsync        = defaultAsync;
        this.metadataLookup      = requireNonNull(metadataLookup, "metadataLookup");
        this.dispatcherLookup    = requireNonNull(dispatcherLookup, "dispatcherLookup");
        this.supervisor          = requireNonNull(supervisor, "supervisor");
        this.eventMetadataLookup = requireNonNull(eventMetadataLookup, "eventMetadataLookup");
        this.logger              = requireNonNull(logger, "logger");
    }

    Class<?> getSubscriberClass(Object subscriber) {
        return subscriber.getClass();
    }

    EventMetadata getEventMetadata(Object event) {
        return eventMetadataLookup.lookup(event)
                .orElseThrow(() -> new IllegalStateException("couldn't lookup metadata for event " + event))
                ;
    }

    Stream<Either<Object, Throwable>> dispatch(Stream<Listener> listeners, Object event) {
        return listeners.map(listener -> listener.dispatcher.dispatch(listener.instance, event));
    }

    List<Object> supervise(Stream<Either<Object, Throwable>> stream, List<Throwable> toDispatch) {
        List<Either<Object, Throwable>> unsupervised = stream.collect(Collectors.toList());
        List<Object> supervised = new ArrayList<>(unsupervised.size());

        loop:
        for (Either<Object, Throwable> e : unsupervised) {
            if (e.isLeft()) {
                supervised.add(e.left());
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

    Optional<Dispatcher> getDispatcher(ListenerMetadata metadata) {
        Optional<Dispatcher> res = Optional.ofNullable(dispatchers.get(metadata));

        if (res.isPresent()) {
            return res;
        }

        res = dispatcherLookup.lookup(metadata);
        res.ifPresent(d -> dispatchers.put(metadata, d));

        return res;
    }

    List<Object> doDispatch(Object event, Stream<Listener> listeners, boolean async) {
        Stream<Either<Object, Throwable>> answers = dispatch(listeners, event);

        List<Throwable> toDispatch = new ArrayList<>();
        List<Object> supervised = supervise(answers, toDispatch);

        if (async) toDispatch.forEach(cause -> publishAsync(new SupervisedEvent(event, cause)));
        else       toDispatch.forEach(cause -> publishSync(new SupervisedEvent(event, cause)));

        return supervised;
    }

    Stream<EventMetadata> resolveHierarchy(EventMetadata lowestEvent) {
        return StreamUtils.collect(Stream.of(lowestEvent), EventMetadata::getParent).distinct();
    }

    Stream<Listener> resolveListeners(EventMetadata meta) {
        return resolveHierarchy(meta).flatMap(it -> listeners.get(it).stream());
    }

    @Override
    public Future<List<Object>> publishAsync(Object event) {
        EventMetadata meta = getEventMetadata(event);
        return worker.submit(() -> doDispatch(event, resolveListeners(meta), true));
    }

    @Override
    public List<Object> publishSync(Object event) {
        return doDispatch(event, resolveListeners(getEventMetadata(event)), false);
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
        metadataLookup.lookup(subscriber)
                .<Listener>flatMap(m -> StreamUtils.asStream(
                        getDispatcher(m).map(d ->
                                new Listener(m, d, subscriber))))
                .forEach(listener -> listeners.put(listener.metadata.getHandledEventMetadata(), listener));

        return this;
    }

    @Override
    public boolean unsubscribe(Object subscriber) {
        boolean removed = false;

        Iterator<Listener> it = listeners.values().iterator();
        while (it.hasNext()) {
            Listener listener = it.next();

            if (listener.instance == subscriber) {
                it.remove();
                removed = true;
            }
        }

        return removed;
    }
}
