package com.github.blackrush.acara;

public final class JavaEventMetadataBuilder implements EventMetadataBuilder {
    @Override
    public EventMetadata build(Object event) {
        return new JavaEventMetadata<>(event.getClass());
    }
}
