package com.github.blackrush.acara.dispatch;

import org.fungsi.Either;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypedDispatcher<T> implements Dispatcher {
    final Class<? extends T> eventClass;

    protected TypedDispatcher() {
        Type tpe = getClass().getGenericSuperclass();

        if (!(tpe instanceof ParameterizedType)) {
            throw new IllegalStateException("can't resolve type parameter from " + tpe);
        }

        Type tpeT = ((ParameterizedType) tpe).getActualTypeArguments()[0];

        if (!(tpeT instanceof Class)) {
            throw new IllegalStateException("can't resolve type parameter of " + tpeT);
        }

        @SuppressWarnings("unchecked")
        Class<? extends T> eventClass = (Class) tpeT;

        this.eventClass = eventClass;
    }

    protected TypedDispatcher(Class<? extends T> eventClass) {
        this.eventClass = eventClass;
    }

    protected abstract Either<Object, Throwable> dispatch0(Object listener, T event);

    @Override
    public Either<Object, Throwable> dispatch(Object listener, Object event) {
        if (!eventClass.isInstance(event)) {
            return Either.failure(new IllegalArgumentException(String.format(
                    "expected a %s but got %s",
                    eventClass,
                    event
            )));
        }

        T evt = eventClass.cast(event);
        return dispatch0(listener, evt);
    }
}
