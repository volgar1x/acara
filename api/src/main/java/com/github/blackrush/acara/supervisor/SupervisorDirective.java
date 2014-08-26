package com.github.blackrush.acara.supervisor;

/**
 * {@link SupervisorDirective} describes actions that need to be done when an exception is caught.
 */
public enum SupervisorDirective {
    /**
     * Re-throw the exception. No warning will be logged.
     */
    ESCALATE,

    /**
     * Simply ignore exception, and continue to dispatch. Logs a warning anyway.
     */
    IGNORE,

    /**
     * Continue to dispatch, and then dispatch a new event containing the cause. No warning will be logged.
     */
    NEW_EVENT,
}
