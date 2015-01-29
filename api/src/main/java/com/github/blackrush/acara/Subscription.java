package com.github.blackrush.acara;

/**
 * A Subscription is a handle to further un-subscribe yourself.
 * @see com.github.blackrush.acara.Subscribable
 */
public interface Subscription {

    /**
     * Revoke the subscription. There may be more than one revocation.
     */
    void revoke();
}
