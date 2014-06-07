package com.github.blackrush.acara;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class StdListenerMetadataLookupTest {

    private StdListenerMetadataLookup lookup;

    @Before
    public void setUp() throws Exception {
        lookup = StdListenerMetadataLookup.SHARED;
    }

    @Test
    public void testLookup() throws Exception {
        // given
        SomeListener listener = new SomeListener();

        // when
        List<ListenerMetadata> res = lookup.lookup(listener).collect(Collectors.toList());

        // then
        assertTrue("lookup result has only one element", res.size() == 1);

        ListenerMetadata metadata = res.get(0);
        assertTrue("metadata's listener class is SomeListener", metadata.getListenerClass() == SomeListener.class);
        assertTrue("metadata's handled event class is SomeEvent", metadata.getHandledEventClass() == SomeEvent.class);
        assertTrue("metadata's listener method's name is \"someListener\"", metadata.getListenerMethod().getName().equals("someListener"));
    }
}