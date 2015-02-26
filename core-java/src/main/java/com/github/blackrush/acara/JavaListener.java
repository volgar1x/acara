package com.github.blackrush.acara;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;

import java.lang.reflect.Method;

public class JavaListener<T> extends Listener {
    final TypedEventMetadata<T> signature;
    final Method behavior;

    public JavaListener(TypedEventMetadata<T> signature, Method behavior) {
        this.signature = signature;
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
    public Future<Object> dispatch(Object state, Object event, Worker worker) {
        @SuppressWarnings("deprecation")
        T evt = signature.cast(event);

        return worker.execute(() -> {
            Object res = this.invoke(state, this.behavior, evt);

            if (res == null) {
                // also applies to methods returning void
                return Futures.success(Unit.instance());
            }

            if (res instanceof Future<?>) {
                @SuppressWarnings("unchecked")
                Future<Object> fut = (Future<Object>) res;
                return fut;
            }

            return Futures.success(res);
        });
    }

    protected Object invoke(Object state, Method behavior, T event) throws Throwable {
        return behavior.invoke(state, event);
    }
}
