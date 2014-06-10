package com.github.blackrush.acara;

public final class StdEventModule implements EventModule {
    @Override
    public EventBusBuilder configure(EventBusBuilder builder) {
        return builder
                .addMetadataLookup(StdListenerMetadataLookup.SHARED)
                .addDispatcherLookup(StdDispatcher.LOOKUP)
                .addEventMetadataLookup(StdEventMetadata.LOOKUP)
                ;
    }
}
