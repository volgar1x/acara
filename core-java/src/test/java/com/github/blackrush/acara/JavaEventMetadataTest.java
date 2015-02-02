package com.github.blackrush.acara;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaEventMetadataTest {

    static class Parent{}
    static class Child extends Parent{}

    @Test
    public void testGetParent() throws Exception {
        // given
        JavaEventMetadata child = new JavaEventMetadata<>(Child.class);

        // when
        EventMetadata parent = child.getParent();
        EventMetadata grandparent = parent.getParent();

        // then
        assertEquals("child's parent", new JavaEventMetadata<>(Parent.class), parent);
        assertEquals("child's grandparent", null, grandparent);
    }
}