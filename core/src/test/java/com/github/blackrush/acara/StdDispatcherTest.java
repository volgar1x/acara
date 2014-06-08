package com.github.blackrush.acara;

import org.fungsi.Either;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StdDispatcherTest {

    @Test
    public void testDispatch() throws Exception {
        // given
        SomeEvent event = new SomeEvent("dispatch");
        SomeListener listener = new SomeListener();

        ListenerMetadata metadata = new ListenerMetadata(
                SomeListener.class,
                SomeListener.class.getDeclaredMethod("someListener", SomeEvent.class),
                new StdEventMetadata(SomeEvent.class)
        );
        StdDispatcher dispatcher = new StdDispatcher(metadata);

        // when
        Either<Object, Throwable> result = dispatcher.dispatch(listener, event);

        // then
        assertTrue("dispatcher result should be a success", result.isLeft());
        assertTrue("dispatcher result should be null", result.left() == null);
        assertTrue("listener handled event is the exact same given event", listener.handled.get() == event);
    }
}