package com.github.blackrush.acara;

public class ThrowingListener {
    @Listener
    public void throwing(SomeEvent evt) {
        throw new Error();
    }
}
