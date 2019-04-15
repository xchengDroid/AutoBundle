package autobundle.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Create bundle by interface method parameters
 * type.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Box {
    /**
     * field bundle key
     */
    String value();

    /**
     * Description of the field
     */
    String desc() default "";

}
