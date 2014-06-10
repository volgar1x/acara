package com.github.blackrush.acara;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventBusImplTest.class,
        StdDispatcherTest.class,
        StdListenerMetadataLookupTest.class,
        StdSupervisorTest.class,
        SupervisedEventMetadataTest.class,
        SuperviseListenerMetadataLookupTest.class,
})
public class AcaraCoreTestSuite {
}
