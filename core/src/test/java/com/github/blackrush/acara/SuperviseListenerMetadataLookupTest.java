package com.github.blackrush.acara;

import com.github.blackrush.acara.supervisor.event.SupervisedEvent;
import com.google.common.util.concurrent.MoreExecutors;
import org.fungsi.Unit;
import org.fungsi.concurrent.Promise;
import org.fungsi.concurrent.Promises;
import org.fungsi.concurrent.Workers;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.fungsi.Unit.unit;
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

    @Test
    public void testEventBus() throws Exception {
        // given
        class SuperviseListener {
            final Promise<Unit> handled = Promises.create();

            @Supervise
            public void npe(NullPointerException npe, SomeEvent evt) {
                handled.complete(unit());
            }
        }

        SuperviseListener listener = new SuperviseListener();

        EventBus eventBus = CoreEventBus.builder()
                .setWorker(Workers.wrap(MoreExecutors.sameThreadExecutor()))
                .isDefaultAsync(false)
                .setMetadataLookup(SuperviseListenerMetadataLookup.SHARED)
                .setDispatcherLookup(SuperviseDispatcher.LOOKUP)
                .setEventMetadataLookup(SupervisedEventMetadata.LOOKUP)
                .build();

        // when
        eventBus.subscribe(listener).publish(new SupervisedEvent(new SomeEvent("event-bus"), new NullPointerException()));

        // then
        assertThat("result is unit", listener.handled.get(Duration.ofMillis(10)), equalTo(unit()));
    }
}