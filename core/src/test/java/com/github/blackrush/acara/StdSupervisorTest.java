package com.github.blackrush.acara;

import com.github.blackrush.acara.supervisor.SupervisorDirective;
import org.junit.Before;
import org.junit.Test;

import static com.github.blackrush.acara.supervisor.SupervisorDirective.ESCALATE;
import static com.github.blackrush.acara.supervisor.SupervisorDirective.IGNORE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
        assertThat("supervisor result", res, is(ESCALATE));
    }

    @Test
    public void testHandleOther() throws Exception {
        // given
        NullPointerException npe = new NullPointerException();

        // when
        SupervisorDirective res = supervisor.handle(npe);

        // then
        assertThat("supervisor result", res, is(IGNORE));
    }
}