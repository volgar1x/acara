package com.github.blackrush.acara;

import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Worker;

import java.lang.reflect.Method;

public class JavaListener extends Listener {
    final EventMetadata signature;
    final Object state;
    final Method behavior;

    public JavaListener(EventMetadata signature, Object state, Method behavior) {
        this.signature = signature;
        this.state = state;
        this.behavior = behavior;
    }

    @Override
    public EventMetadata getHandledEvent() {
        return signature;
    }

    @Override
    public Future<Object> dispatch(Object event, Worker worker) {
        return worker.submit(() -> this.invoke(state, behavior, event));
    }

    protected Object invoke(Object state, Method behavior, Object event) throws Throwable {
        return behavior.invoke(state, event);
    }
}