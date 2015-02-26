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
    public TypedEventMetadata<T> getHandledEvent() {
        return signature;
    }

    public Method getBehavior() {
        return behavior;
    }

    @Override
    public Future<Object> dispatch(Object event, Worker worker) {
        @SuppressWarnings("deprecation")
        T evt = signature.cast(event);

        return worker.submit(() -> this.invoke(state, behavior, evt));
    }

    protected Object invoke(Object state, Method behavior, T event) throws Throwable {
        return behavior.invoke(state, event);
    }
}
