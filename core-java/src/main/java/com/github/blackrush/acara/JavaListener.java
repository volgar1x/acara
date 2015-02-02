package com.github.blackrush.acara;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Worker;

import java.lang.reflect.Method;

public class JavaListener<T> extends Listener {
    final TypedEventMetadata<T> signature;
    final Object state;
    final Method behavior;

    public JavaListener(TypedEventMetadata<T> signature, Object state, Method behavior) {
        this.signature = signature;
        this.state = state;
        this.behavior = behavior;
    }

    @Override
    public EventMetadata getHandledEvent() {
        return signature;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Future<Object> dispatch(Object event, Worker worker) {
        return worker.submit(() -> this.invoke(state, behavior, signature.cast(event)));
    }

    protected Object invoke(Object state, Method behavior, T event) throws Throwable {
        return behavior.invoke(state, event);
    }
}
