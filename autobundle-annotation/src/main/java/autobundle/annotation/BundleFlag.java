package autobundle.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark a method  for the specified value
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface BundleFlag {
    /**
     * mark the bundle
     */
    int value();

    /**
     * Description of the bundle
     */
    String desc() default "";

}
