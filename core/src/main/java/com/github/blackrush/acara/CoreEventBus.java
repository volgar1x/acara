package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;
import org.fungsi.concurrent.Worker;

import static java.util.Objects.requireNonNull;

public final class CoreEventBus {
    private CoreEventBus() {}

    public static EventBus create(Worker worker, boolean defaultAsync, ListenerMetadataLookup metadataLookup, DispatcherLookup dispatcherLookup, Supervisor supervisor) {
        return new EventBusImpl(worker, defaultAsync, metadataLookup, dispatcherLookup, supervisor);
    }

    public static EventBus create(Worker worker, boolean defaultAsync) {
        return create(worker, defaultAsync, StdListenerMetadataLookup.SHARED, StdDispatcher.LOOKUP, StdSupervisor.SHARED);
    }

    public static EventBus create(Worker worker) {
        return create(worker, true);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Worker worker;
        private boolean defaultAsync = true;
        private ListenerMetadataLookup metadataLookup = StdListenerMetadataLookup.SHARED;
        private DispatcherLookup dispatcherLookup = StdDispatcher.LOOKUP;
        private Supervisor supervisor = StdSupervisor.SHARED;

        public Builder setWorker(Worker worker) {
            this.worker = requireNonNull(worker, "worker");
            return this;
        }

        public Builder isDefaultAsync(boolean defaultAsync) {
            this.defaultAsync = defaultAsync;
            return this;
        }

        public Builder setMetadataLookup(ListenerMetadataLookup metadataLookup) {
            this.metadataLookup = metadataLookup;
            return this;
        }

        public Builder setDispatcherLookup(DispatcherLookup dispatcherLookup) {
            this.dispatcherLookup = dispatcherLookup;
            return this;
        }

        public Builder setSupervisor(Supervisor supervisor) {
            this.supervisor = supervisor;
            return this;
        }

        public EventBus build() {
            requireNonNull(worker, "worker");
            return create(worker, defaultAsync, metadataLookup, dispatcherLookup, supervisor);
        }
    }
}
