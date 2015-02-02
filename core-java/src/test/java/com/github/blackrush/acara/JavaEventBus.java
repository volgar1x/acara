package com.github.blackrush.acara;

import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.fungsi.function.UnsafeRunnable;
import org.fungsi.function.UnsafeSupplier;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class JavaEventBus {

    static class TheEvent {
        final String name;
        TheEvent(String name) { this.name = name; }
    }

    static class FunkyEvent extends TheEvent {
        FunkyEvent(String name) { super(name); }
    }

    @Retention(RetentionPolicy.RUNTIME)
    static @interface Funky{}

    static class FunkyListenerBuilder extends JavaListenerBuilder {
        @Override
        protected Stream<Listener> scan(Object o, Method method) {
            if (!method.isAnnotationPresent(Funky.class)) {
                return Stream.empty();
            }
            JavaListener listener = new JavaListener(new JavaEventMetadata(FunkyEvent.class), o, method) {
                @Override
                protected Object invoke(Object state, Method behavior, Object event) throws Throwable {
                    FunkyEvent evt = (FunkyEvent) event;
                    return behavior.invoke(state, evt.name);
                }
            };
            return Stream.of(listener);
        }
    }

    static class TheListener {
        @Listen
        public String listen(TheEvent evt) {
            return String.format("Hello, %s!", evt.name);
        }

        @Funky
        public String funky(String name) {
            return String.format("Wassup %s?", name);
        }
    }

    @Test
    public void testSubscribePublishRevoke() throws Exception {

        EventBus eventBus = new EventBusImpl(
                new JavaEventMetadataBuilder(),
                new JavaListenerBuilder().concat(new FunkyListenerBuilder()),
                new SameThreadWorker());

        Subscription sub = eventBus.subscribe(new TheListener());
        String msg1 = (String) eventBus.publish(new TheEvent("World")).get().get(0);
        String msg2 = (String) eventBus.publish(new FunkyEvent("dawg")).get().get(0);
        sub.revoke();

        assertEquals("first message", "Hello, World!", msg1);
        assertEquals("second message", "Wassup dawg?", msg2);
    }

    class SameThreadWorker implements Worker {

        @Override
        public <T> Future<T> execute(UnsafeSupplier<Future<T>> fn) {
            try {
                return fn.get();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        @Override
        public <T> Future<T> submit(UnsafeSupplier<T> fn) {
            try {
                return Futures.success(fn.get());
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        @Override
        public Future<Unit> cast(UnsafeRunnable fn) {
            try {
                fn.run();
                return Futures.unit();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }
}
