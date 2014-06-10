package com.github.blackrush.acara;

import com.github.blackrush.acara.supervisor.event.SupervisedEvent;
import org.junit.Before;
import org.junit.Test;

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
        assertThat("handled initial event event",res.handledInitialEventClass, equalTo(SomeEvent.class));
    }
}