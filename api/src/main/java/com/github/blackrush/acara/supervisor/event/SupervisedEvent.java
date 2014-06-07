package com.github.blackrush.acara.supervisor.event;

public final class SupervisedEvent {
    private final Object initialEvent;
    private final Throwable cause;

    public SupervisedEvent(Object initialEvent, Throwable cause) {
        this.initialEvent = initialEvent;
        this.cause = cause;
    }

    public Object getInitialEvent() {
        return initialEvent;
    }

    public Throwable getCause() {
        return cause;
    }
}
