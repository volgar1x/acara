package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.Dispatcher;
import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import org.fungsi.Either;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;

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
    final DispatcherLookup dispatcherLookup;
    final Supervisor supervisor;

    final Map<ListenerMetadata, Dispatcher> dispatchers     = new HashMap<>();
    final ListMultimap<Class<?>, Listener>  listeners       = Multimaps.newListMultimap(new IdentityHashMap<>(), ArrayList::new);
    final Set<Class<?>>                     deadSubscribers = Sets.newIdentityHashSet();

    EventBusImpl(Worker worker, boolean defaultAsync, ListenerMetadataLookup metadataLookup, DispatcherLookup dispatcherLookup, Supervisor supervisor) {
        this.worker           = requireNonNull(worker, "worker");
        this.defaultAsync     = defaultAsync;
        this.metadataLookup   = requireNonNull(metadataLookup, "metadataLookup");
        this.dispatcherLookup = requireNonNull(dispatcherLookup, "dispatcherLookup");
        this.supervisor       = requireNonNull(supervisor, "supervisor");
    }

    Class<?> getSubscriberClass(Object subscriber) {
        return subscriber.getClass();
    }

    Class<?> getEventClass(Object event) {
        return event.getClass();
    }

    Stream<Either<Object, Throwable>> dispatch(Stream<Listener> listeners, Object event) {
        return listeners.map(listener -> listener.dispatcher.dispatch(listener.instance, event));
    }

    List<ListenerMetadata> getListenerMetadata(Object subscriber) {
        return metadataLookup.lookup(subscriber).collect(Collectors.toList());
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

    @Override
    public Future<List<Object>> publishAsync(Object event) {
        Class<?> eventClass = getEventClass(event);
        Collection<Listener> listeners = this.listeners.get(eventClass);

        return worker.submit(() -> {
            Stream<Either<Object, Throwable>> answers = dispatch(listeners.stream(), event);
            return answers.collect(Collectors.toList());
        });
    }

    @Override
    public List<Object> publishSync(Object event) {
        Class<?> eventClass = getEventClass(event);
        Collection<Listener> listeners = this.listeners.get(eventClass);

        Stream<Either<Object, Throwable>> answers = dispatch(listeners.stream(), event);
        return answers.collect(Collectors.toList());
    }

    @Override
    public Future<List<Object>> publish(Object event) {
        if (defaultAsync) {
            return publishAsync(event);
        } else {
            return Futures.success(publishSync(event));
        }
    }

    @Override
    public EventBus subscribe(Object subscriber) {
        metadataLookup.lookup(subscriber)
                .<Listener>flatMap(m -> StreamUtils.asStream(
                        getDispatcher(m).map(d ->
                                new Listener(m, d, subscriber))))
                .forEach(listener -> listeners.put(listener.metadata.getHandledEventClass(), listener));

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
