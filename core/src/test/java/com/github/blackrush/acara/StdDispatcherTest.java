package com.github.blackrush.acara;

import org.fungsi.Either;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
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
        assertTrue("dispatcher result is a success", result.isLeft());
        assertThat("dispatcher result", result.left(), is(nullValue()));
        assertThat("listener handled event", listener.handled.get(), equalTo(event));
    }
}