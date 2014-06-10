package com.github.blackrush.acara;

public class ChildEvent extends SomeEvent {
    final int integer;

    public ChildEvent(String someValue, int integer) {
        super(someValue);
        this.integer = integer;
    }
}
