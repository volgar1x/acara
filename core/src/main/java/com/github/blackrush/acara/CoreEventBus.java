package com.github.blackrush.acara;

import com.github.blackrush.acara.dispatch.DispatcherLookup;
import com.github.blackrush.acara.supervisor.Supervisor;
import org.fungsi.concurrent.Worker;

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
     * @param metadataLookup a non-null {@link com.github.blackrush.acara.ListenerMetadataLookup}
     * @param dispatcherLookup a non-null {@link com.github.blackrush.acara.dispatch.DispatcherLookup}
     * @param supervisor a non-null {@link com.github.blackrush.acara.supervisor.Supervisor}
     * @return a non-null {@link com.github.blackrush.acara.EventBus}
     */
    public static EventBus create(Worker worker, boolean defaultAsync, ListenerMetadataLookup metadataLookup, DispatcherLookup dispatcherLookup, Supervisor supervisor) {
        return new EventBusImpl(worker, defaultAsync, metadataLookup, dispatcherLookup, supervisor);
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
        return create(worker, defaultAsync, StdListenerMetadataLookup.SHARED, StdDispatcher.LOOKUP, StdSupervisor.SHARED);
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
     * <p>
     *     Properties :
     *     <ul>
     *         <li><em>worker</em> <strong>required</strong></li>
     *         <li><em>defaultAsync</em> default to {@code true}</li>
     *         <li><em>metadataLookup</em> default to {@code StdListenerMetadataLookup.SHARED}</li>
     *         <li><em>dispatcherLookup</em> default to {@code StdDispatcher.LOOKUP}</li>
     *         <li><em>supervisor</em> default to {@code StdSupervisor.SHARED}</li>
     *     </ul>
     * </p>
     */
    public static class Builder {
        private Worker worker;
        private boolean defaultAsync = true;
        private ListenerMetadataLookup metadataLookup = StdListenerMetadataLookup.SHARED;
        private DispatcherLookup dispatcherLookup = StdDispatcher.LOOKUP;
        private Supervisor supervisor = StdSupervisor.SHARED;

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
         * Set `dispatcherLookup` property. Default to {@code StdDispatcher.LOOKUP}.
         * @param dispatcherLookup a non-null {@link com.github.blackrush.acara.dispatch.DispatcherLookup}
         * @return the very same builder
         */
        public Builder setDispatcherLookup(DispatcherLookup dispatcherLookup) {
            this.dispatcherLookup = dispatcherLookup;
            return this;
        }

        /**
         * Set `supervisor` property. Default to {@code StdSupervisor.SHARED}.
         * @param supervisor a non-null {@link com.github.blackrush.acara.supervisor.Supervisor}
         * @return the very same builder
         */
        public Builder setSupervisor(Supervisor supervisor) {
            this.supervisor = supervisor;
            return this;
        }

        /**
         * Build a very new {@link com.github.blackrush.acara.EventBus}
         * @return a non-null {@link com.github.blackrush.acara.EventBus}
         * @throws java.lang.NullPointerException if missing properties
         */
        public EventBus build() {
            requireNonNull(worker, "worker");
            return create(worker, defaultAsync, metadataLookup, dispatcherLookup, supervisor);
        }
    }
}
