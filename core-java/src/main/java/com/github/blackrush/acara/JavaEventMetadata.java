package com.github.blackrush.acara;

public final class JavaEventMetadata extends EventMetadata {
    final Class<?> klass;

    public JavaEventMetadata(Class<?> klass) {
        this.klass = klass;
    }

    @Override
    public EventMetadata getParent() {
        if (klass.getSuperclass() == Object.class) {
            return null;
        }
        return new JavaEventMetadata(klass.getSuperclass());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (o == this) return true;
        JavaEventMetadata other = (JavaEventMetadata) o;
        return other.klass.equals(this.klass);
    }

    @Override
    public int hashCode() {
        return klass.hashCode();
    }
}
