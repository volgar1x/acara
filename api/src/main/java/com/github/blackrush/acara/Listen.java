package com.github.blackrush.acara;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark a method as an event listener.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Listen {
    /**
     * Enable or disable this method. {@code false} by default.
     * @return whether or not the method is disabled
     */
    boolean disabled() default false;
}
