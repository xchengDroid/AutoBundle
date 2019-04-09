package autobundle.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field  for the specified value. The field will automatically be cast to the field
 * type.
 * <pre><code>
 * {@literal @}BindData boolean xxx;
 * </code></pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface BooleanValue {
    /**
     * field bundle key
     */
    String value();

    /**
     * Description of the field
     */
    String desc() default "";
}
