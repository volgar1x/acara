package com.github.blackrush.acara;

/**
 * {@inheritDoc}
 * This module installs all components needed to make {@link com.github.blackrush.acara.Listener} listeners work.
 */
public final class StdEventModule implements EventModule {
    /**
     * {@inheritDoc}
     */
    @Override
    public EventBusBuilder configure(EventBusBuilder builder) {
        return builder
                .addMetadataLookup(StdListenerMetadataLookup.newDefault())
                .addDispatcherLookup(StdDispatcher.LOOKUP)
                .addEventMetadataLookup(StdEventMetadata.LOOKUP)
                ;
    }
}
