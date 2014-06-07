package com.github.blackrush.acara;

import com.github.blackrush.acara.supervisor.Supervisor;
import com.github.blackrush.acara.supervisor.SupervisorDirective;

import static com.github.blackrush.acara.supervisor.SupervisorDirective.ESCALATE;
import static com.github.blackrush.acara.supervisor.SupervisorDirective.IGNORE;

/**
 * {@link com.github.blackrush.acara.StdSupervisor} escalates every {@link java.lang.Error} and ignores other.
 */
public class StdSupervisor implements Supervisor {
    public static final StdSupervisor SHARED = new StdSupervisor();

    /**
     * {@inheritDoc}
     */
    @Override
    public SupervisorDirective handle(Throwable cause) {
        if (cause instanceof Error) {
            return ESCALATE;
        }

        return IGNORE;
    }
}
