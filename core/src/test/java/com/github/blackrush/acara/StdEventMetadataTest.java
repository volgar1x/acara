package com.github.blackrush.acara;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class StdEventMetadataTest {

    class BothEvent extends SomeEvent implements EventIface {

        public BothEvent(String someValue) {
            super(someValue);
        }
    }

    @Test
    public void testDummyEventParent() throws Exception {
        // given
        StdEventMetadata meta = new StdEventMetadata(SomeEvent.class, StdEventMetadata.LOOKUP);

        // when
        List<Class<?>> parent = meta.getParent().map(EventMetadata::getRawEventClass).collect(Collectors.toList());

        // then
        assertThat("parent size", parent.size(), equalTo(0));
    }

    @Test
    public void testExtendingEventParent() throws Exception {
        // given
        StdEventMetadata meta = new StdEventMetadata(ChildEvent.class, StdEventMetadata.LOOKUP);

        // when
        List<Class<?>> parent = meta.getParent().map(EventMetadata::getRawEventClass).collect(Collectors.toList());

        // then
        assertThat("parent size", parent.size(), equalTo(1));
        assertThat("parent event class", parent.get(0), equalTo(SomeEvent.class));
    }

    @Test
    public void testImplementingEventParent() throws Exception {
        // given
        StdEventMetadata meta = new StdEventMetadata(ImplementingEvent.class, StdEventMetadata.LOOKUP);

        // when
        List<Class<?>> parent = meta.getParent().map(EventMetadata::getRawEventClass).collect(Collectors.toList());

        // then
        assertThat("parent size", parent.size(), equalTo(1));
        assertThat("parent event class", parent.get(0), equalTo(EventIface.class));
    }

    @Test
    public void testBothEventParent() throws Exception {
        // given
        StdEventMetadata meta = new StdEventMetadata(BothEvent.class, StdEventMetadata.LOOKUP);

        // when
        List<Class<?>> parent = meta.getParent().map(EventMetadata::getRawEventClass).collect(Collectors.toList());

        // then
        assertThat("parent size", parent.size(), equalTo(2));
        assertThat("parent event classes", parent, hasItem(SomeEvent.class));
        assertThat("parent event classes", parent, hasItem(EventIface.class));
    }
}