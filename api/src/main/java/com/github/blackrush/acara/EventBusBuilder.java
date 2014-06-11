package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;

import java.util.function.UnaryOperator;

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
     * Get current {@link com.github.blackrush.acara.ListenerMetadataLookup} which might be used by the final building {@link com.github.blackrush.acara.EventBus}
     * @return a non-null lookup
     */
    ListenerMetadataLookup getMetadataLookup();

    /**
     * Concat current {@link com.github.blackrush.acara.ListenerMetadataLookup} with another one.
     * @param metadataLookup a non-null lookup
     * @return a non-null builder
     */
    EventBusBuilder addMetadataLookup(ListenerMetadataLookup metadataLookup);

    /**
     * Get current {@link com.github.blackrush.acara.ListenerMetadataLookup}, apply an operator on it, and add it back.
     * @param fn a non-null operator
     * @return a non-null builder
     */
    default EventBusBuilder wrapMetadataLookup(UnaryOperator<ListenerMetadataLookup> fn) {
        return addMetadataLookup(fn.apply(getMetadataLookup()));
    }




    /**
     * Get current {@link com.github.blackrush.acara.dispatch.DispatcherLookup} which might be used by the final building {@link com.github.blackrush.acara.EventBus}
     * @return a non-null lookup
     */
    DispatcherLookup getDispatcherLookup();

    /**
     * Aggregate current {@link com.github.blackrush.acara.dispatch.DispatcherLookup} with another one through
     * {@link com.github.blackrush.acara.dispatch.DispatcherLookup#withFallback(com.github.blackrush.acara.dispatch.DispatcherLookup)}
     * @param dispatcherLookup a non-null lookup
     * @return a non-null builder
     */
    EventBusBuilder addDispatcherLookup(DispatcherLookup dispatcherLookup);

    /**
     * Get current {@link com.github.blackrush.acara.dispatch.DispatcherLookup}, apply an operator on it, and add it back.
     * @param fn a non-null operator
     * @return a non-null builder
     */
    default EventBusBuilder wrapDispatcherLookup(UnaryOperator<DispatcherLookup> fn) {
        return addDispatcherLookup(fn.apply(getDispatcherLookup()));
    }




    /**
     * Get current {@link com.github.blackrush.acara.EventMetadataLookup} which might be used by the final building {@link com.github.blackrush.acara.EventBus}
     * @return a non-null lookup
     */
    EventMetadataLookup getEventMetadataLookup();

    /**
     * Aggregate current {@link com.github.blackrush.acara.EventMetadataLookup} with another one through
     * {@link com.github.blackrush.acara.EventMetadataLookup#withFallback(EventMetadataLookup)}
     * @param eventMetadataLookup a non-null lookup
     * @return a non-null builder
     */
    EventBusBuilder addEventMetadataLookup(EventMetadataLookup eventMetadataLookup);

    /**
     * Get current {@link com.github.blackrush.acara.EventMetadataLookup}, apply an operator on it, and add it back.
     * @param fn a non-null operator
     * @return a non-null builder
     */
    default EventBusBuilder wrapEventMetadataLookup(UnaryOperator<EventMetadataLookup> fn) {
        return addEventMetadataLookup(fn.apply(getEventMetadataLookup()));
    }




    /**
     * Get current {@link com.github.blackrush.acara.supervisor.Supervisor} which might be used by the final building {@link com.github.blackrush.acara.EventBus}
     * @return a non-null supervisor
     */
    Supervisor getSupervisor();

    /**
     * Set current {@link com.github.blackrush.acara.supervisor.Supervisor}. This method is destructive as the previous
     * supervisor won't be used by the building {@link com.github.blackrush.acara.EventBus} anymore.
     * @param supervisor a non-null supervisor
     * @return a non-null builder
     */
    EventBusBuilder setSupervisor(Supervisor supervisor);

    /**
     * Get current {@link com.github.blackrush.acara.supervisor.Supervisor}, apply an operator on it, and set it back.
     * @param fn a non-null operator
     * @return a non-null builder
     */
    default EventBusBuilder wrapSupervisor(UnaryOperator<Supervisor> fn) {
        return setSupervisor(fn.apply(getSupervisor()));
    }




    /**
     * Build a fresh new {@link com.github.blackrush.acara.EventBus}. This method is guaranteed side-effect free.
     * @return a non-null event bus
     */
    EventBus build();
}
