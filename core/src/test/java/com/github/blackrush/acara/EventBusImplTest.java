package com.github.blackrush.acara;

import org.fungsi.concurrent.Workers;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

public class EventBusImplTest {

    private EventBusImpl eventBus;

    @Before
    public void setUp() throws Exception {
        eventBus = new EventBusImpl(
                Workers.wrap(Executors.newSingleThreadExecutor()),
                false,
                StdListenerMetadataLookup.SHARED,
                StdDispatcher.LOOKUP,
                StdSupervisor.SHARED,
                LoggerFactory.getLogger(EventBusImpl.class)
        );
    }

    @Test
    public void testPublishAsync() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-async");
        SomeListener listener = new SomeListener();

        // when
        eventBus.subscribe(listener).publishAsync(event);

        // then
        SomeEvent handled = listener.handled.get(Duration.ofMillis(1));
        assertTrue("handled == event", handled == event);
    }

    @Test
    public void testPublishSync() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-sync");
        SomeListener listener = new SomeListener();

        // when
        eventBus.subscribe(listener).publishSync(event);

        // then
        SomeEvent handled = listener.handled.get(Duration.ofMillis(10));
        assertTrue("handled == event", handled == event);
    }

    @Test
    public void testSubscribe() throws Exception {
        // given
        class AnotherEvent { }

        class AnotherListener {
            @Listener
            public void listen(SomeEvent evt) {}

            @Listener
            public void listen(AnotherEvent evt) {}
        }

        SomeListener listener1 = new SomeListener();
        SomeListener listener2 = new SomeListener();
        SomeListener listener3 = new SomeListener();
        AnotherListener listener4 = new AnotherListener();

        // when
        eventBus.subscribe(listener1);
        eventBus.subscribe(listener2);
        eventBus.subscribe(listener3);
        eventBus.subscribe(listener4);

        // then
        assertTrue("eventBus.listeners size equal to 3", eventBus.listeners.size() == 5);
    }

    @Test
    public void testUnsubscribe() throws Exception {
        // given
        SomeListener listener = new SomeListener();

        // when
        eventBus.subscribe(listener).unsubscribe(listener);

        // then
        assertTrue("eventBus.listeners is empty", eventBus.listeners.isEmpty());
    }
}