package com.github.blackrush.acara.supervisor;

/**
 * {@link Supervisor} maps a {@link java.lang.Throwable} to a {@link SupervisorDirective}.
 */
public interface Supervisor {
    /**
     * Map {@link java.lang.Throwable} to {@link SupervisorDirective}
     * @param cause a non-null throwable
     * @return a non-null directive
     */
    SupervisorDirective handle(Throwable cause);
}
