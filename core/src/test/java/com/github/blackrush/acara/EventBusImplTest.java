package com.github.blackrush.acara;

import com.github.blackrush.acara.supervisor.Supervisor;
import com.github.blackrush.acara.supervisor.SupervisorDirective;
import com.github.blackrush.acara.supervisor.event.SupervisedEvent;
import org.fungsi.concurrent.Promise;
import org.fungsi.concurrent.Promises;
import org.fungsi.concurrent.Workers;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

import static java.time.Duration.ofMillis;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventBusImplTest {

    private EventBusImpl eventBus;
    private Supervisor supervisor;

    @Before
    public void setUp() throws Exception {
        supervisor = mock(Supervisor.class);
        eventBus = new EventBusImpl(
                Workers.wrap(Executors.newSingleThreadExecutor()),
                false,
                StdListenerMetadataLookup.SHARED,
                StdDispatcher.LOOKUP,
                supervisor,
                StdEventMetadata.LOOKUP,
                LoggerFactory.getLogger(EventBusImpl.class)
        );
    }

    @Test
    public void testPublishAsync() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-async");
        SomeListener listener = new SomeListener();

        // when
        eventBus.subscribe(listener).publishAsync(event);

        // then
        SomeEvent handled = listener.handled.get(ofMillis(10));
        assertThat("handled event", handled, equalTo(event));
    }

    @Test
    public void testPublishSync() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-sync");
        SomeListener listener = new SomeListener();

        // when
        eventBus.subscribe(listener).publishSync(event);

        // then
        SomeEvent handled = listener.handled.get(ofMillis(10));
        assertThat("handled event", handled, equalTo(event));
    }

    @Test
    public void testPublishAndAnswer() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-sync");
        Object listener = new Object() {
            @Listener
            public String listen(SomeEvent evt) {
                return evt.someValue;
            }
        };

        // when
        List<Object> answers = eventBus.subscribe(listener).publishSync(event);

        // then
        assertThat("answers size", answers.size(), equalTo(1));
        assertThat("first answer", answers.get(0), equalTo(event.someValue));
    }

    @Test
    public void testPublishAndNoAnswer() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-sync-no-answer");
        SomeListener listener = new SomeListener();

        // when
        List<Object> answers = eventBus.subscribe(listener).publishSync(event);

        // then
        assertThat("answers size", answers.size(), equalTo(0));
    }

    @Test
    public void testPublishChildEvent() throws Exception {
        // given
        ChildEvent event = new ChildEvent("child", 42);
        SomeListener listener = new SomeListener();

        // when
        eventBus.subscribe(listener).publishSync(event);

        // then
        assertThat("handled event", listener.handled.get(Duration.ofMillis(10)), instanceOf(ChildEvent.class));
    }

    @Test
    public void testPublishImplementingEvent() throws Exception {
        // given
        class MyListener {
            final Promise<EventIface> handled = Promises.create();

            @Listener
            public void eventIface(EventIface evt) {
                handled.complete(evt);
            }
        }

        EventIface event = new ImplementingEvent();
        MyListener listener = new MyListener();

        // when
        eventBus.subscribe(listener).publishSync(event);

        // then
        assertThat("handled event", listener.handled.get(Duration.ofMillis(10)), instanceOf(ImplementingEvent.class));
    }

    @Test(expected = Error.class)
    public void testPublishAndSuperviseEscalate() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-and-supervise-escalate");
        ThrowingListener listener = new ThrowingListener();

        // when
        when(supervisor.handle(any(Error.class))).thenReturn(SupervisorDirective.ESCALATE);

        eventBus.subscribe(listener).publishSync(event);

        // then

    }

    @Test
    public void testPublishAndSuperviseIgnore() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-and-supervise-ignore");
        ThrowingListener listener = new ThrowingListener();

        // when
        when(supervisor.handle(any(Error.class))).thenReturn(SupervisorDirective.IGNORE);
        List<Object> answers = eventBus.subscribe(listener).publishSync(event);

        // then
        assertTrue("answers list is empty", answers.isEmpty());
    }

    @Test
    public void testPublishAndSuperviseStop() throws Exception {
        // given
        SomeEvent event = new SomeEvent("publish-and-supervise-stop");
        ThrowingListener listener1 = new ThrowingListener();
        SomeListener listener2 = new SomeListener();

        // when
        when(supervisor.handle(any(Error.class))).thenReturn(SupervisorDirective.STOP);
        List<Object> answers = eventBus.subscribe(listener1).subscribe(listener2).publishSync(event);

        // then
        assertTrue("answers list is empty", answers.isEmpty());
    }

    @Test
    public void testPublishAndSuperviseNewEvent() throws Exception {
        // given
        class HandleSupervisedEventListener {
            Promise<SupervisedEvent> handled = Promises.create();

            @Listener
            public void supervisedEvent(SupervisedEvent evt) {
                handled.complete(evt);
            }
        }

        SomeEvent event = new SomeEvent("publish-and-supervise-new-event");
        ThrowingListener listener1 = new ThrowingListener();
        HandleSupervisedEventListener listener2 = new HandleSupervisedEventListener();

        // when
        when(supervisor.handle(any(Error.class))).thenReturn(SupervisorDirective.NEW_EVENT);
        List<Object> answers = eventBus.subscribe(listener1).subscribe(listener2).publishSync(event);

        // then
        assertTrue("answers list is empty", answers.isEmpty());
        assertThat("supervised initial event", listener2.handled.get(ofMillis(10)).getInitialEvent(), equalTo(event));
    }

    @Test
    public void testSubscribe() throws Exception {
        // given
        class AnotherEvent { }

        class AnotherListener {
            @Listener
            public void listen(SomeEvent evt) {}

            @Listener
            public void listen(AnotherEvent evt) {}
        }

        SomeListener listener1 = new SomeListener();
        SomeListener listener2 = new SomeListener();
        SomeListener listener3 = new SomeListener();
        AnotherListener listener4 = new AnotherListener();

        // when
        eventBus.subscribe(listener1);
        eventBus.subscribe(listener2);
        eventBus.subscribe(listener3);
        eventBus.subscribe(listener4);

        // then
        assertThat("eventBus listeners list size", eventBus.listeners.size(), equalTo(5));
    }

    @Test
    public void testUnsubscribe() throws Exception {
        // given
        SomeListener listener = new SomeListener();

        // when
        eventBus.subscribe(listener).unsubscribe(listener);

        // then
        assertTrue("eventBus listeners list is empty", eventBus.listeners.isEmpty());
    }
}