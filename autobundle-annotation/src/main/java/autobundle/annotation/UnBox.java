package autobundle.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bind a field  for the specified value. The field will automatically be cast to the field
 * type.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface UnBox {
    /**
     * field bundle key
     */
    String value();

    /**
     * Description of the field
     */
    String desc() default "";

}
