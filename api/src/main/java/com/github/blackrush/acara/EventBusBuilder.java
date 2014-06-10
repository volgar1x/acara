package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;

public interface EventBusBuilder {
    default EventBusBuilder install(EventModule module) {
        return module.configure(this);
    }

    EventBusBuilder addMetadataLookup(ListenerMetadataLookup metadataLookup);
    EventBusBuilder addDispatcherLookup(DispatcherLookup dispatcherLookup);
    EventBusBuilder addEventMetadataLookup(EventMetadataLookup eventMetadataLookup);
    EventBusBuilder setSupervisor(Supervisor supervisor);

    EventBus build();
}
