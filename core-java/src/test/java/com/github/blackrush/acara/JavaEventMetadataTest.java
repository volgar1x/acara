package com.github.blackrush.acara;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaEventMetadataTest {

    static class Parent{}
    static class Child extends Parent{}

    interface ParentInterface{}
    static class OtherChild implements ParentInterface{}

    @Test
    public void testGetParent() throws Exception {
        // given
        JavaEventMetadata<Child> child = new JavaEventMetadata<>(Child.class);

        // when
        JavaEventMetadata<?> parent = child.getParent();
        assert parent != null;
        JavaEventMetadata<?> grandparent = parent.getParent();

        // then
        assertEquals("child's parent", new JavaEventMetadata<>(Parent.class), parent);
        assertEquals("child's grandparent", null, grandparent);
    }

    @Test
    public void testGetInterfaceParent() throws Exception {
        // given
        JavaEventMetadata<OtherChild> child = new JavaEventMetadata<>(OtherChild.class);

        // when
        JavaEventMetadata<?> parent = child.getParent();
        assert parent != null;

        // then
        assertEquals("parent class", ParentInterface.class, parent.klass);
    }
}