package com.github.blackrush.acara;

import org.fungsi.concurrent.Workers;
import org.junit.Before;
import org.junit.Test;

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
                StdSupervisor.SHARED
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
        SomeEvent handled = listener.handled.get(Duration.ofMillis(1));
        assertTrue("handled == event", handled == event);
    }

    @Test
    public void testSubscribe() throws Exception {
        // given
        SomeListener listener = new SomeListener();

        // when
        eventBus.subscribe(listener);

        // then
        assertTrue("eventBus.listeners is not empty", !eventBus.listeners.isEmpty());
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