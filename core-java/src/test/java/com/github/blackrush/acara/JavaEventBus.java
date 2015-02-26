package com.github.blackrush.acara;

import com.google.common.util.concurrent.MoreExecutors;
import org.fungsi.Unit;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Workers;
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

    static enum UnwrapFutureEvent { instance }
    static enum VoidListenerEvent { instance }

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
            JavaEventMetadata<FunkyEvent> meta = new JavaEventMetadata<>(FunkyEvent.class);

            JavaListener<FunkyEvent> listener = new JavaListener<FunkyEvent>(meta, o, method) {
                @Override
                protected Object invoke(Object state, Method behavior, FunkyEvent event) throws Throwable {
                    return behavior.invoke(state, event.name);
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

        @Listen
        public Future<String> unwrapFuture(UnwrapFutureEvent evt) {
            return Futures.success("foobar");
        }

        @Listen
        public void voidListener(VoidListenerEvent evt) {}
    }

    @Test
    public void testSubscribePublishRevoke() throws Exception {

        EventBus eventBus = new EventBusImpl(
                new JavaEventMetadataBuilder(),
                new JavaListenerBuilder().concat(new FunkyListenerBuilder()),
                Workers.wrap(MoreExecutors.directExecutor()));

        Subscription sub = eventBus.subscribe(new TheListener());
        String msg1 = (String) eventBus.publish(new TheEvent("World")).get().get(0);
        String msg2 = (String) eventBus.publish(new FunkyEvent("dawg")).get().get(0);
        String msg3 = (String) eventBus.publish(UnwrapFutureEvent.instance).get().get(0);
        Unit msg4 = (Unit) eventBus.publish(VoidListenerEvent.instance).get().get(0);
        sub.revoke();

        assertEquals("first message", "Hello, World!", msg1);
        assertEquals("second message", "Wassup dawg?", msg2);
        assertEquals("third message", "foobar", msg3);
        assertEquals("fourth message", Unit.instance(), msg4);
    }

}
