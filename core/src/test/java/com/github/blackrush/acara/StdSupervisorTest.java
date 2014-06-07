package com.github.blackrush.acara;

import com.github.blackrush.acara.supervisor.SupervisorDirective;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StdSupervisorTest {

    private StdSupervisor supervisor;

    @Before
    public void setUp() throws Exception {
        supervisor = new StdSupervisor();

    }

    @Test
    public void testHandleError() throws Exception {
        // given
        Error error = new Error();

        // when
        SupervisorDirective res = supervisor.handle(error);

        // then
        assertTrue("supervisor result is IGNORE", res == SupervisorDirective.ESCALATE);
    }

    @Test
    public void testHandleOther() throws Exception {
        // given
        NullPointerException npe = new NullPointerException();

        // when
        SupervisorDirective res = supervisor.handle(npe);

        // then
        assertTrue("supervisor result is IGNORE", res == SupervisorDirective.IGNORE);
    }
}