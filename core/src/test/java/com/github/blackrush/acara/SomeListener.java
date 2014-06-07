package com.github.blackrush.acara;

import org.fungsi.concurrent.Promise;
import org.fungsi.concurrent.Promises;

public class SomeListener {

    public final Promise<SomeEvent> handled = Promises.create();

    @Listener
    public void someListener(SomeEvent evt) {
        handled.complete(evt);
    }
}
