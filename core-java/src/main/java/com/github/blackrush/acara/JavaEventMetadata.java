package com.github.blackrush.acara;

public final class JavaEventMetadata<T> extends TypedEventMetadata<T> {
    final Class<T> klass;

    public JavaEventMetadata(Class<T> klass) {
        this.klass = klass;
    }

    @Override
    public JavaEventMetadata<? super T> getParent() {
        Class<? super T> superclass = klass.getSuperclass();

        if (superclass == Object.class) {
            return null;
        }

        return new JavaEventMetadata<>(superclass);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (o == this) return true;
        JavaEventMetadata<?> other = (JavaEventMetadata<?>) o;
        return other.klass.equals(this.klass);
    }

    @Override
    public int hashCode() {
        return klass.hashCode();
    }
}
