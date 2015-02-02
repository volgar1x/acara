package com.github.blackrush.acara;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaEventMetadataBuilderTest {

    JavaEventMetadataBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new JavaEventMetadataBuilder();
    }

    @Test
    public void testBuild() throws Exception {
        // given
        Object event = "lol";

        // when
        EventMetadata meta = builder.build(event);

        // then
        assertEquals(new JavaEventMetadata(String.class), meta);
    }
}