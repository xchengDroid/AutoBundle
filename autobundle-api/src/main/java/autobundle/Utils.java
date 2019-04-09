package autobundle;

import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import autobundle.annotation.BooleanArrayValue;
import autobundle.annotation.BooleanValue;
import autobundle.annotation.ByteArrayValue;
import autobundle.annotation.ByteValue;
import autobundle.annotation.CharArrayValue;
import autobundle.annotation.CharSequenceArrayListValue;
import autobundle.annotation.CharSequenceArrayValue;
import autobundle.annotation.CharSequenceValue;
import autobundle.annotation.CharValue;
import autobundle.annotation.DoubleArrayValue;
import autobundle.annotation.DoubleValue;
import autobundle.annotation.FloatArrayValue;
import autobundle.annotation.FloatValue;
import autobundle.annotation.IntArrayValue;
import autobundle.annotation.IntValue;
import autobundle.annotation.IntegerArrayListValue;
import autobundle.annotation.LongArrayValue;
import autobundle.annotation.LongValue;
import autobundle.annotation.ParcelableArrayListValue;
import autobundle.annotation.ParcelableArrayValue;
import autobundle.annotation.ParcelableValue;
import autobundle.annotation.SerializableValue;
import autobundle.annotation.ShortArrayValue;
import autobundle.annotation.ShortValue;
import autobundle.annotation.SparseParcelableArrayValue;
import autobundle.annotation.StringArrayListValue;
import autobundle.annotation.StringArrayValue;
import autobundle.annotation.StringValue;

/**
 * 创建时间：2019/4/9
 * 编写人： chengxin
 * 功能描述：
 */
public class Utils {
    public static final List<Class<? extends Annotation>> ANNOTATIONS = Arrays.asList(//
            BooleanArrayValue.class,
            BooleanValue.class,
            ByteArrayValue.class,
            ByteValue.class,
            CharArrayValue.class,
            CharSequenceArrayListValue.class,
            CharSequenceArrayValue.class,
            CharSequenceValue.class,
            CharValue.class,
            DoubleArrayValue.class,
            DoubleValue.class,
            FloatArrayValue.class,
            FloatValue.class,
            IntArrayValue.class,
            IntegerArrayListValue.class,
            IntValue.class,
            LongArrayValue.class,
            LongValue.class,
            ParcelableArrayListValue.class,
            ParcelableArrayValue.class,
            ParcelableValue.class,
            SerializableValue.class,
            ShortArrayValue.class,
            ShortValue.class,
            SparseParcelableArrayValue.class,
            StringArrayListValue.class,
            StringArrayValue.class,
            StringValue.class
    );

    static RuntimeException methodError(Method method, String message, Object... args) {
        return methodError(method, null, message, args);
    }

    static RuntimeException methodError(Method method, @Nullable Throwable cause, String message,
                                        Object... args) {
        message = String.format(message, args);
        return new IllegalArgumentException(message
                + "\n    for method "
                + method.getDeclaringClass().getSimpleName()
                + "."
                + method.getName(), cause);
    }

    static RuntimeException parameterError(Method method,
                                           Throwable cause, int p, String message, Object... args) {
        return methodError(method, cause, message + " (parameter #" + (p + 1) + ")", args);
    }

    static RuntimeException parameterError(Method method, int p, String message, Object... args) {
        return methodError(method, message + " (parameter #" + (p + 1) + ")", args);
    }
}
