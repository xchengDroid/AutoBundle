package autobundle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 创建时间：2019/4/11
 * 编写人： chengxin
 * 功能描述：call bundle.putXXX() method in{@link #apply(Bundle, String, Object, boolean)}
 */
public interface ParameterHandler<T> {
    void apply(Bundle bundle, String key, @Nullable T value, boolean required);

    /**
     * Creates {@link ParameterHandler} instances based on a type and target usage.
     */
    abstract class Factory {

        /**
         * Returns a {@link ParameterHandler} for applying {@code type} to an Bundle value, or null if
         * {@code type} cannot be handled by this factory. This is used to create parameterHandler for types
         * specified by {@link autobundle.annotation.Box @Box} values.
         */
        @Nullable
        public abstract ParameterHandler<?> get(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations);

        /**
         * Extract the upper bound of the generic parameter at {@code index} from {@code type}. For
         * example, index 1 of {@code Map<String, ? extends Runnable>} returns {@code Runnable}.
         */
        protected static Type getParameterUpperBound(int index, ParameterizedType type) {
            return Utils.getParameterUpperBound(index, type);
        }

        /**
         * Extract the raw class type from {@code type}. For example, the type representing
         * {@code List<? extends Runnable>} returns {@code List.class}.
         */
        protected static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
    }

}
