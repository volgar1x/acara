package com.github.blackrush.acara;

import org.junit.Test;
import org.mockito.InOrder;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

public class EventMetadataBuilderTest {

    // Y U DO DIS MOCKITO
    class Stub implements EventMetadataBuilder {
        @Override
        public EventMetadata build(Object o) {
            return null;
        }

        @Override
        public EventMetadataBuilder withFallback(EventMetadataBuilder other) {
            return EventMetadataBuilder.super.withFallback(other);
        }
    }

    @Test
    public void testWithFallback() throws Exception {
        // given
        Stub first = spy(new Stub());
        Stub second = spy(new Stub());
        Stub third = spy(new Stub());

        EventMetadataBuilder acc = first;
        acc = acc.withFallback(second);
        acc = acc.withFallback(third);

        Object event = new Object();

        // when
        EventMetadata meta = acc.build(event);

        // then
        assertNull(meta);

        InOrder o = inOrder(first, second, third);
        o.verify(first).build(event);
        o.verify(second).build(event);
        o.verify(third).build(event);
        o.verifyNoMoreInteractions();
    }
}