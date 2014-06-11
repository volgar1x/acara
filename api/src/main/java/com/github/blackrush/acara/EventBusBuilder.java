package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;

/**
 * An {@link com.github.blackrush.acara.EventBusBuilder} is responsible of building new {@link com.github.blackrush.acara.EventBus}
 */
public interface EventBusBuilder {
    /**
     * Install an {@link com.github.blackrush.acara.EventModule} to the building {@link com.github.blackrush.acara.EventBus}
     * @param module a non-null module
     * @return a non-null builder
     */
    default EventBusBuilder install(EventModule module) {
        return module.configure(this);
    }

    /**
     * Concat current {@link com.github.blackrush.acara.ListenerMetadataLookup} with another one.
     * @param metadataLookup a non-null lookup
     * @return a non-null builder
     */
    EventBusBuilder addMetadataLookup(ListenerMetadataLookup metadataLookup);

    /**
     * Aggregate current {@link com.github.blackrush.acara.dispatch.DispatcherLookup} with another one through
     * {@link com.github.blackrush.acara.dispatch.DispatcherLookup#withFallback(com.github.blackrush.acara.dispatch.DispatcherLookup)}
     * @param dispatcherLookup a non-null lookup
     * @return a non-null builder
     */
    EventBusBuilder addDispatcherLookup(DispatcherLookup dispatcherLookup);

    /**
     * Aggregate current {@link com.github.blackrush.acara.EventMetadataLookup} with another one through
     * {@link com.github.blackrush.acara.EventMetadataLookup#withFallback(EventMetadataLookup)}
     * @param eventMetadataLookup a non-null lookup
     * @return a non-null builder
     */
    EventBusBuilder addEventMetadataLookup(EventMetadataLookup eventMetadataLookup);

    /**
     * Set current {@link com.github.blackrush.acara.supervisor.Supervisor}. This method is destructive as the previous
     * supervisor won't be used by the building {@link com.github.blackrush.acara.EventBus} anymore.
     * @param supervisor a non-null supervisor
     * @return a non-null builder
     */
    EventBusBuilder setSupervisor(Supervisor supervisor);

    /**
     * Build a fresh new {@link com.github.blackrush.acara.EventBus}. This method is guaranteed side-effect free.
     * @return a non-null event bus
     */
    EventBus build();
}
