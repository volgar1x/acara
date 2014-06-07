package com.github.blackrush.acara;

final class ExceptionUtils {
    private ExceptionUtils() {}

    public static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) throw new NullPointerException("t");
        ExceptionUtils.<RuntimeException>sneakyThrow0(t);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
        throw (T)t;
    }
}
