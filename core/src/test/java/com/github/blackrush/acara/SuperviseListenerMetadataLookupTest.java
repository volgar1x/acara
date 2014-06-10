package com.github.blackrush.acara;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SuperviseListenerMetadataLookupTest {
    private SuperviseListenerMetadataLookup lookup;

    @Before
    public void setUp() throws Exception {
        lookup = new SuperviseListenerMetadataLookup();
    }

    @Test
    public void testSuccessfulLookup() throws Exception {
        // given
        class ValidSuperviseListener {
            @Supervise
            public void npe(NullPointerException npe, Object initialEvent) {

            }
        }
        Object listener = new ValidSuperviseListener();

        // when
        List<ListenerMetadata> res = lookup.lookup(listener).collect(toList());

        // then
        assertThat("result size", res.size(), equalTo(1));

        ListenerMetadata metadata = res.get(0);
        assertThat("listener metadata method name", metadata.getListenerMethod().getName(), equalTo("npe"));
        assertThat("listener event metadata", metadata.getHandledEventMetadata(), instanceOf(SupervisedEventMetadata.class));
    }

    @Test
    public void testUnsuccessfulLookup() throws Exception {
        // given
        Object listener = new Object();

        // when
        List<ListenerMetadata> res = lookup.lookup(listener).collect(toList());

        // then
        assertThat("result size", res.size(), equalTo(0));
    }
}