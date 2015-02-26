package com.github.blackrush.acara;

import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Worker;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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
        when(listener1.dispatch(any(), any(), any())).thenReturn(Futures.success(new Object()));

        Listener listener2 = mock(Listener.class);
        when(listener2.getHandledEvent()).thenReturn(meta2);
        when(listener2.dispatch(any(), any(), any())).thenReturn(Futures.success(new Object()));

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

    @Test
    public void testSubscribeManyPublishRevoke() throws Exception {
        Object object = new Object(), object2 = new Object(), object3 = new Object();

        EventMetadata meta1 = mock(EventMetadata.class),
                meta2 = mock(EventMetadata.class),
                meta3 = mock(EventMetadata.class);

        Listener listener1 = mock(Listener.class);
        when(listener1.getHandledEvent()).thenReturn(meta1);
        when(listener1.dispatch(any(), any(), any())).thenReturn(Futures.success(new Object()));

        Listener listener2 = mock(Listener.class);
        when(listener2.getHandledEvent()).thenReturn(meta2);
        when(listener2.dispatch(any(), any(), any())).thenReturn(Futures.success(new Object()));

        Listener listener3 = mock(Listener.class);
        when(listener3.getHandledEvent()).thenReturn(meta3);
        when(listener3.dispatch(any(), any(), any())).thenReturn(Futures.success(new Object()));

        when(listenerBuilder.build(object)).thenReturn(Stream.of(listener1, listener2));
        when(listenerBuilder.build(object2)).thenReturn(Stream.of(listener1));
        when(listenerBuilder.build(object3)).thenReturn(Stream.of(listener3));

        when(eventMetadataBuilder.build("foobar")).thenReturn(meta1);
        when(eventMetadataBuilder.build("buzz")).thenReturn(meta2);
        when(eventMetadataBuilder.build("qux")).thenReturn(meta3);

        Subscription sub = eventBus.subscribeMany(Arrays.asList(object, object2, object3));
        List<Object> buzz = eventBus.publish("buzz").get();
        List<Object> qux = eventBus.publish("qux").get();
        sub.revoke();
        List<Object> foobar = eventBus.publish("foobar").get();

        assertEquals("foobar responses", 0, foobar.size());
        assertEquals("buzz responses", 1, buzz.size());
        assertEquals("qux responses", 1, qux.size());
    }

    @Test
    public void testEventBubbling() throws Exception {
        EventMetadata parent = mock(EventMetadata.class);
        EventMetadata child = mock(EventMetadata.class);

        when(child.getParent()).thenReturn(parent);

        when(eventMetadataBuilder.build("parent")).thenReturn(parent);
        when(eventMetadataBuilder.build("child")).thenReturn(child);

        Object state1 = new Object();
        Object state2 = new Object();

        Listener lParent = mock(Listener.class), lChild = mock(Listener.class);
        when(lParent.getHandledEvent()).thenReturn(parent);
        when(lParent.dispatch(any(), eq("parent"), any())).thenReturn(Futures.success(new Object()));
        when(lParent.dispatch(any(), eq("child"), any())).thenReturn(Futures.success(new Object()));

        when(lChild.getHandledEvent()).thenReturn(child);
        when(lChild.dispatch(any(), eq("child"), any())).thenReturn(Futures.success(new Object()));

        when(listenerBuilder.build(state1)).thenReturn(Stream.of(lParent, lChild));
        when(listenerBuilder.build(state2)).thenReturn(Stream.of(lParent));

        Subscription sub1 = eventBus.subscribe(state1);
        Subscription sub2 = eventBus.subscribe(state2);
        List<Object> responses1 = eventBus.publish("child").get();
        List<Object> responses2 = eventBus.publish("parent").get();
        sub1.revoke();
        sub2.revoke();

        verify(lParent, times(2)).dispatch(any(), eq("parent"), any());
        verify(lParent, times(1)).dispatch(any(), eq("child"), any());
        verify(lChild, times(1)).dispatch(any(), eq("child"), any());

        assertEquals("first number of responses", 2, responses1.size());
        assertEquals("second number of responses", 2, responses2.size());
    }
}