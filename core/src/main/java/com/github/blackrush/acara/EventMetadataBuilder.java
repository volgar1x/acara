package com.github.blackrush.acara;

@FunctionalInterface
public interface EventMetadataBuilder {
    /**
     * Build the metadata of the given event.
     * @param o a non-null event
     * @return the builded metadata or null if this builder does not care about the event
     */
    EventMetadata build(Object o);

    default EventMetadataBuilder withFallback(EventMetadataBuilder other) {
        return o -> {
            EventMetadata meta = this.build(o);
            if (meta != null) {
                return meta;
            }
            return other.build(o);
        };
    }
}
