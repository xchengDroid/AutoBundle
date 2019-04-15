package autobundle.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a method  for the specified value. The field will automatically be cast to the field
 * type.
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
