package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;
import org.fungsi.concurrent.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

/**
 * {@link com.github.blackrush.acara.CoreEventBus} is a static factory of {@link com.github.blackrush.acara.EventBus}.
 *
 * @see com.github.blackrush.acara.EventBus
 */
public final class CoreEventBus {
    private CoreEventBus() {}

    /**
     * Create an {@link com.github.blackrush.acara.EventBus} with given components and properties.
     * @param worker a non-null {@link org.fungsi.concurrent.Worker} used to dispatch asynchronous publications
     * @param defaultAsync if {@literal true}, asynchronously dispatch by default, synchronously else
     * @param metadataLookup a non-null {@link ListenerMetadataLookup}
     * @param dispatcherLookup a non-null {@link com.github.blackrush.acara.dispatch.DispatcherLookup}
     * @param supervisor a non-null {@link com.github.blackrush.acara.supervisor.Supervisor}
     * @param eventMetadataLookup a non-null {@link com.github.blackrush.acara.EventMetadataLookup}
     * @param logger a non-null {@link org.slf4j.Logger}
     * @return a non-null {@link com.github.blackrush.acara.EventBus}
     */
    public static EventBus create(Worker worker, boolean defaultAsync, ListenerMetadataLookup metadataLookup, DispatcherLookup dispatcherLookup, Supervisor supervisor, EventMetadataLookup eventMetadataLookup, Logger logger) {
        return new EventBusImpl(worker, defaultAsync, metadataLookup, dispatcherLookup, supervisor, eventMetadataLookup, logger);
    }

    /**
     * Create an {@link com.github.blackrush.acara.EventBus} with given {@link org.fungsi.concurrent.Worker} and standards
     * lookups and supervisor.
     * @param worker a non-null {@link org.fungsi.concurrent.Worker} used to dispatch asynchronous publications
     * @param defaultAsync if {@literal true}, asynchronously dispatch by default, synchronously else
     * @return a non-null {@link com.github.blackrush.acara.EventBus}
     * @see com.github.blackrush.acara.StdDispatcher
     * @see com.github.blackrush.acara.StdListenerMetadataLookup
     * @see com.github.blackrush.acara.StdSupervisor
     */
    public static EventBus create(Worker worker, boolean defaultAsync) {
        return create(worker, defaultAsync,
                StdListenerMetadataLookup.SHARED,
                StdDispatcher.LOOKUP,
                StdSupervisor.SHARED,
                StdEventMetadata.LOOKUP,
                LoggerFactory.getLogger(EventBusImpl.class)
        );
    }

    /**
     * Create an {@link com.github.blackrush.acara.EventBus} with given {@link org.fungsi.concurrent.Worker} that
     * asynchronously dispatch by default, and use standards lookups and supervisor.
     * @param worker a non-null {@link org.fungsi.concurrent.Worker} used to dispatch asynchronous publications
     * @return a non-null {@link com.github.blackrush.acara.EventBus}
     * @see com.github.blackrush.acara.StdDispatcher
     * @see com.github.blackrush.acara.StdListenerMetadataLookup
     * @see com.github.blackrush.acara.StdSupervisor
     */
    public static EventBus create(Worker worker) {
        return create(worker, true);
    }

    /**
     * Create a new fluent builder of {@link com.github.blackrush.acara.EventBus}
     * @return a non-null builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link com.github.blackrush.acara.CoreEventBus.Builder} fluently builds {@link com.github.blackrush.acara.EventBus}.
     *
     *     <table summary="Properties">
     *         <tr><td>worker</td><td><strong>required</strong></td></tr>
     *         <tr><td>defaultAsync</td><td>default to {@code true}</td></tr>
     *         <tr><td>metadataLookup</td><td>default to {@code StdListenerMetadataLookup.SHARED}</td></tr>
     *         <tr><td>dispatcherLookup</td><td>default to {@code StdDispatcher.LOOKUP}</td></tr>
     *         <tr><td>supervisor</td><td>default to {@code StdSupervisor.SHARED}</td></tr>
     *         <tr><td>logger</td><td>default to {@code LoggerFactory.getLogger(EventBusImpl.class)}</td></tr>
     *     </table>
     */
    public static class Builder implements EventBusBuilder {
        private Worker worker;
        private boolean defaultAsync = true;
        private ListenerMetadataLookup metadataLookup = StdListenerMetadataLookup.SHARED;
        private DispatcherLookup dispatcherLookup = StdDispatcher.LOOKUP;
        private Supervisor supervisor = StdSupervisor.SHARED;
        private EventMetadataLookup eventMetadataLookup = StdEventMetadata.LOOKUP;
        private Logger logger = LoggerFactory.getLogger(EventBusImpl.class);

        /**
         * Set `worker` property. Required.
         * @param worker a non-null {@link org.fungsi.concurrent.Worker} used to dispatch asynchronous publications
         * @return the very same builder
         */
        public Builder setWorker(Worker worker) {
            this.worker = requireNonNull(worker, "worker");
            return this;
        }

        /**
         * Set `defaultAsync` property. Default to {@code true}.
         * @param defaultAsync if {@literal true}, asynchronously dispatch by default, synchronously else
         * @return the very same builder
         */
        public Builder isDefaultAsync(boolean defaultAsync) {
            this.defaultAsync = defaultAsync;
            return this;
        }

        /**
         * Set `metadataLookup` property. Default to {@code StdListenerMetadataLookup.SHARED}.
         * @param metadataLookup a non-null {@link com.github.blackrush.acara.ListenerMetadataLookup}
         * @return the very same builder
         */
        public Builder setMetadataLookup(ListenerMetadataLookup metadataLookup) {
            this.metadataLookup = metadataLookup;
            return this;
        }

        /**
         * Concat current `metadataLookup` with another {@link com.github.blackrush.acara.ListenerMetadataLookup}
         * @param metadataLookup a non-null {@link com.github.blackrush.acara.ListenerMetadataLookup}
         * @return the very same builder
         */
        @Override
        public Builder addMetadataLookup(ListenerMetadataLookup metadataLookup) {
            this.metadataLookup = this.metadataLookup.concat(metadataLookup);
            return this;
        }

        /**
         * Set `dispatcherLookup` property. Default to {@code StdDispatcher.LOOKUP}.
         * @param dispatcherLookup a non-null {@link com.github.blackrush.acara.dispatch.DispatcherLookup}
         * @return the very same builder
         */
        public Builder setDispatcherLookup(DispatcherLookup dispatcherLookup) {
            this.dispatcherLookup = dispatcherLookup;
            return this;
        }

        /**
         * Set `dispatcherLookup` property with current `dispatcherLookup` as fallback.
         * @param dispatcherLookup a non-null {@link com.github.blackrush.acara.dispatch.DispatcherLookup}
         * @return the very same builder
         */
        @Override
        public Builder addDispatcherLookup(DispatcherLookup dispatcherLookup) {
            this.dispatcherLookup = dispatcherLookup.withFallback(this.dispatcherLookup);
            return this;
        }

        /**
         * Set `supervisor` property. Default to {@code StdSupervisor.SHARED}.
         * @param supervisor a non-null {@link com.github.blackrush.acara.supervisor.Supervisor}
         * @return the very same builder
         */
        @Override
        public Builder setSupervisor(Supervisor supervisor) {
            this.supervisor = supervisor;
            return this;
        }

        /**
         * Set `eventMetadataLookup` property. Default to {@code StdEventMetadata.LOOKUP}.
         * @param eventMetadataLookup a non-null {@link com.github.blackrush.acara.EventMetadataLookup}
         * @return the very same builder
         */
        public Builder setEventMetadataLookup(EventMetadataLookup eventMetadataLookup) {
            this.eventMetadataLookup = eventMetadataLookup;
            return this;
        }

        /**
         * Set `eventMetadataLookup` property with current `eventMetadataLookup` as fallback.
         * @param eventMetadataLookup a non-null {@link com.github.blackrush.acara.EventMetadataLookup}
         * @return the very same builder
         */
        @Override
        public Builder addEventMetadataLookup(EventMetadataLookup eventMetadataLookup) {
            this.eventMetadataLookup = eventMetadataLookup.withFallback(this.eventMetadataLookup);
            return this;
        }

        /**
         * Set `logger` property. Default to {@code LoggerFactory.getLogger(EventBusImpl.class)}
         * @param logger a non-null {@link org.slf4j.Logger}
         * @return the very same builder
         */
        public Builder setLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Build a very new {@link com.github.blackrush.acara.EventBus}
         * @return a non-null {@link com.github.blackrush.acara.EventBus}
         * @throws java.lang.NullPointerException if missing properties
         */
        @Override
        public EventBus build() {
            requireNonNull(worker, "worker");
            return create(worker, defaultAsync, metadataLookup, dispatcherLookup, supervisor, eventMetadataLookup, logger);
        }
    }
}
