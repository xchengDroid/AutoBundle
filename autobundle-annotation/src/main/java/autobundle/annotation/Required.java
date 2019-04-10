
package autobundle.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * If required, app will be crash when value is null.
 * Primitive type wont be check!
 */
@Retention(RUNTIME)
@Target({PARAMETER, FIELD})
public @interface Required {
}
