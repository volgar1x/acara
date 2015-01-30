package com.github.blackrush.acara;

import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventBusImplTest {

    EventMetadataBuilder eventMetadataBuilder;
    ListenerBuilder listenerBuilder;
    Worker worker;

    EventBusImpl eventBus;

    @Before
    public void setUp() throws Exception {
        eventMetadataBuilder = mock(EventMetadataBuilder.class);
        listenerBuilder = mock(ListenerBuilder.class);
        worker = mock(Worker.class);

        eventBus = new EventBusImpl(eventMetadataBuilder, listenerBuilder, worker);
    }

    @Test
    public void testSubscribePublishRevoke() throws Exception {
        Object object = new Object();

        EventMetadata meta1 = mock(EventMetadata.class),
                    meta2 = mock(EventMetadata.class),
                    meta3 = mock(EventMetadata.class);

        Listener listener1 = mock(Listener.class);
        when(listener1.getHandledEvent()).thenReturn(meta1);
        when(listener1.dispatch(any(), any())).thenReturn(Futures.success(new Object()));

        Listener listener2 = mock(Listener.class);
        when(listener2.getHandledEvent()).thenReturn(meta2);
        when(listener2.dispatch(any(), any())).thenReturn(Futures.success(new Object()));

        when(listenerBuilder.build(object)).thenReturn(Stream.of(listener1, listener2));

        when(eventMetadataBuilder.build("foobar")).thenReturn(meta1);
        when(eventMetadataBuilder.build("buzz")).thenReturn(meta2);
        when(eventMetadataBuilder.build("qux")).thenReturn(meta3);


        // subscribe
        Subscription sub = eventBus.subscribe(object);
        // publish -- HAS listeners
        List<Object> responses = eventBus.publish("foobar").get();
        // revoke
        sub.revoke();
        // publish -- HAD listeners
        List<Object> responses2 = eventBus.publish("buzz").get();
        // publish -- never had any listeners
        List<Object> responses3 = eventBus.publish("qux").get();


        assertEquals("first  number of responses", responses.size(),  1);
        assertEquals("second number of responses", responses2.size(), 0);
        assertEquals("third  number of responses", responses3.size(), 0);
    }
}