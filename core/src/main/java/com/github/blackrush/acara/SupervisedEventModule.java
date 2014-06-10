package com.github.blackrush.acara;

public final class SupervisedEventModule implements EventModule {
    @Override
    public EventBusBuilder configure(EventBusBuilder builder) {
        return builder
                .addMetadataLookup(SuperviseListenerMetadataLookup.SHARED)
                .addDispatcherLookup(SuperviseDispatcher.LOOKUP)
                .addEventMetadataLookup(SupervisedEventMetadata.LOOKUP)
                ;
    }
}
