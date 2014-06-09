package com.github.blackrush.acara.dispatch;

import org.fungsi.Either;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TypedDispatcherTest {
    @Test
    public void testInstantiationViaClass() throws Exception {
        class StringDispatcher extends TypedDispatcher<String> {
            @Override
            protected Either<Object, Throwable> dispatch0(Object listener, String event) {
                return Either.left(null);
            }
        }

        TypedDispatcher<String> d = new StringDispatcher();

        assertThat("dispatcher event class", d.eventClass, equalTo(String.class));
    }

    @Test
    public void testInstantiationViaAnonymous() throws Exception {
        TypedDispatcher<String> d = new TypedDispatcher<String>() {
            @Override
            protected Either<Object, Throwable> dispatch0(Object listener, String event) {
                return Either.left(null);
            }
        };

        assertThat("dispatcher event class", d.eventClass, equalTo(String.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testInstantiationError() throws Exception {
        class InvalidDispatcher<T> extends TypedDispatcher<T> {
            @Override
            protected Either<Object, Throwable> dispatch0(Object listener, T event) {
                return Either.left(null);
            }
        }

        new InvalidDispatcher<String>();
    }
}