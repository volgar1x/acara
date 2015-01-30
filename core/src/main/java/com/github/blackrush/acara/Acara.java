package com.github.blackrush.acara;

import org.fungsi.concurrent.Worker;

@SuppressWarnings("UnusedDeclaration")
public final class Acara {
    private Acara() {}

    public static EventBus newEventBus(EventMetadataBuilder eventMetadataBuilder, ListenerBuilder listenerBuilder, Worker worker) {
        return new EventBusImpl(eventMetadataBuilder, listenerBuilder, worker);
    }
}
