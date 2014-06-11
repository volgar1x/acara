package com.github.blackrush.acara;

/**
 * {@inheritDoc}
 * This module installs all components needed to make {@link com.github.blackrush.acara.Supervise} listeners work.
 */
public final class SupervisedEventModule implements EventModule {
    /**
     * {@inheritDoc}
     */
    @Override
    public EventBusBuilder configure(EventBusBuilder builder) {
        return builder
                .addMetadataLookup(SuperviseListenerMetadataLookup.SHARED)
                .addDispatcherLookup(SuperviseDispatcher.LOOKUP)
                .addEventMetadataLookup(SupervisedEventMetadata.LOOKUP)
                ;
    }
}
