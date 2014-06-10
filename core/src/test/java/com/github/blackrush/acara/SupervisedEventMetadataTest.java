package com.github.blackrush.acara;

import com.github.blackrush.acara.supervisor.event.SupervisedEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SupervisedEventMetadataTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testLookup() throws Exception {
        // given
        SupervisedEvent event = new SupervisedEvent(new SomeEvent("lookup"), new NullPointerException());

        // when
        SupervisedEventMetadata res = (SupervisedEventMetadata) SupervisedEventMetadata.LOOKUP.lookup(event).get();

        // then
        assertThat("handled cause class", res.handledCauseClass, equalTo(NullPointerException.class));
        assertThat("handled initial event event", res.handledInitialEventClass, equalTo(SomeEvent.class));
    }

    @Test
    public void testGetParent() throws Exception {
        // given
        SupervisedEventMetadata meta = new SupervisedEventMetadata(NullPointerException.class, SomeEvent.class);

        // when
        List<EventMetadata> res = StreamUtils.collect(Stream.of(meta), EventMetadata::getParent)
                .distinct()
                .collect(toList());

        // then
        assertThat("result size", res.size(), equalTo(8));
    }
}