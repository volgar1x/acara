package com.github.blackrush.acara;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class StdListenerMetadataLookupTest {

    private StdListenerMetadataLookup lookup;

    @Before
    public void setUp() throws Exception {
        lookup = StdListenerMetadataLookup.newDefault();
    }

    @Test
    public void testLookup() throws Exception {
        // given
        SomeListener listener = new SomeListener();

        // when
        List<ListenerMetadata> res = lookup.lookup(listener).collect(Collectors.toList());

        // then
        assertThat("lookup result", res.size(), equalTo(1));

        ListenerMetadata metadata = res.get(0);
        assertThat("metadata listener class", metadata.getListenerClass(), equalTo(SomeListener.class));
        assertThat("metadata handled event metadata raw class", metadata.getHandledEventMetadata().getRawEventClass(), equalTo(SomeEvent.class));
        assertThat("metadata listener method name", metadata.getListenerMethod().getName(), equalTo("someListener"));
    }
}