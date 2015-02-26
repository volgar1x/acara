package com.github.blackrush.acara;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JavaListenerBuilderTest {

    JavaListenerBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new JavaListenerBuilder();
    }

    static class TheEvent{}
    static class TheListener {
        @Listen
        public void listen(TheEvent evt) {}

        @Listen
        public void INVALID(){}
    }

    @Test
    public void testBuild() throws Exception {
        // given
        TheListener the = new TheListener();

        // when
        List<Listener> listeners = builder.build(the).collect(Collectors.toList());
        JavaListener<?> first = (JavaListener<?>) listeners.get(0);

        // then
        assertEquals("number of listeners", 1, listeners.size());

        assertEquals("listener's handled event", new JavaEventMetadata<>(TheEvent.class),
                                                    listeners.get(0).getHandledEvent());

        assertEquals("listener's method", first.getBehavior(), TheListener.class.getMethod("listen", TheEvent.class));
    }
}