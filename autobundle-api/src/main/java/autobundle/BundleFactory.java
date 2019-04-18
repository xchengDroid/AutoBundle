package autobundle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import autobundle.ParameterHandler.Factory;
import autobundle.annotation.Box;
import autobundle.annotation.BundleFlag;
import autobundle.annotation.Required;

import static autobundle.Utils.parameterError;

/**
 * 创建时间：2019/4/11
 * 编写人： chengxin
 * 功能描述：create bundle by interface method
 */
final class BundleFactory {

    private static final Map<Method, BundleFactory> bundleFactoryCache = new ConcurrentHashMap<>();

    static BundleFactory loadBundleFactory(Method method) {
        BundleFactory result = bundleFactoryCache.get(method);
        if (result != null) return result;
        synchronized (bundleFactoryCache) {
            result = bundleFactoryCache.get(method);
            if (result == null) {
                result = BundleFactory.parseAnnotations(method);
                bundleFactoryCache.put(method, result);
            }
        }
        return result;
    }

    private static BundleFactory parseAnnotations(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType != Bundle.class) {
            throw Utils.methodError(method, "Service methods must return Bundle.");
        }
        return new Builder(method).build();
    }

    private final ParameterHandler<?>[] parameterHandlers;
    private final Method method;
    private final int bundleFlag;
    private final KeyRequired[] keyRequires;


    private BundleFactory(Method method, ParameterHandler<?>[] parameterHandlers, KeyRequired[] keyRequires, int bundleFlag) {
        this.method = method;
        this.parameterHandlers = parameterHandlers;
        this.bundleFlag = bundleFlag;
        this.keyRequires = keyRequires;
    }

    Bundle invoke(Object[] args) {
        @SuppressWarnings("unchecked") // It is an error to invoke a method with the wrong arg types.
                ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) parameterHandlers;
        int argumentCount = args.length;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + handlers.length + ")");
        }
        Bundle bundle = new Bundle();
        for (int p = 0; p < argumentCount; p++) {
            ParameterHandler<Object> handler = handlers[p];
            KeyRequired keyRequired = keyRequires[p];
            handler.apply(bundle, keyRequired.key, args[p], keyRequired.required);
            for (OnBundleListener listener : AutoBundle.getInstance().listeners) {
                listener.onBundling(bundleFlag, keyRequired.key, args[p], keyRequired.required);
            }
            printInvoke(p, keyRequired, args[p]);
        }
        for (OnBundleListener listener : AutoBundle.getInstance().listeners) {
            listener.onCompleted(bundleFlag, bundle);
        }
        return bundle;
    }

    private void printInvoke(int p, KeyRequired keyRequired, Object arg) {
        if (AutoBundle.getInstance().debug) {
            Log.d(AutoBundle.TAG, "Bundling key: \"" + keyRequired.key + "\", value: " + arg + ", required: " + keyRequired.required
                    + " \n in parameter #" + (p + 1)
                    + " for method "
                    + method.getDeclaringClass().getSimpleName()
                    + "."
                    + method.getName()
            );
        }
    }

    /**
     * Inspects the annotations on an interface method to construct a reusable service method. This
     * requires potentially-expensive reflection so it is best to build each service method only once
     * and reuse it. Builders cannot be reused.
     */
    static final class Builder {
        final Method method;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotationsArray;
        final Type[] parameterTypes;
        ParameterHandler<?>[] parameterHandlers;
        int bundleFlag = Integer.MIN_VALUE;// by default
        KeyRequired[] keyRequires;

        Builder(Method method) {
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterTypes = method.getGenericParameterTypes();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
            for (Annotation annotation : methodAnnotations) {
                if (annotation instanceof BundleFlag) {
                    bundleFlag = ((BundleFlag) annotation).value();
                }
            }
        }

        BundleFactory build() {
            int parameterCount = parameterAnnotationsArray.length;
            parameterHandlers = new ParameterHandler<?>[parameterCount];
            keyRequires = new KeyRequired[parameterCount];
            for (int p = 0; p < parameterCount; p++) {
                parameterHandlers[p] = parseParameter(p, parameterTypes[p], parameterAnnotationsArray[p]);
            }
            return new BundleFactory(method, parameterHandlers, keyRequires, bundleFlag);
        }

        @NonNull
        private ParameterHandler<?> parseParameter(
                int p, Type parameterType, @Nullable Annotation[] annotations) {
            Box boxAnnotation = findBoxAnnotation(annotations);
            if (boxAnnotation == null) {
                throw parameterError(method, p, "@%s annotation not found.", Box.class.getSimpleName());
            }
            boolean required = required(annotations);
            keyRequires[p] = new KeyRequired(boxAnnotation.value(), required);
            printParseParameter(p, boxAnnotation, required);
            ParameterHandler<?> result = null;
            List<Factory> factories = AutoBundle.getInstance().factories;
            for (Factory factory : factories) {
                result = factory.get(parameterType, annotations, methodAnnotations);
                if (result != null) {
                    break;
                }
            }
            if (result == null) {
                throw parameterError(method, p, "'" + Utils.typeToString(parameterType)
                        + "' doesn't support.");
            }

            return result;
        }

        private void printParseParameter(int p, Box boxAnnotation, boolean required) {
            // annotation.getClass -->class com.sun.proxy.$Proxy 动态代理
            if (AutoBundle.getInstance().debug) {
                String boxString = "@" + Box.class.getSimpleName() + "(value= \"" + boxAnnotation.value() + "\" )";
                Log.d(AutoBundle.TAG, "Parse " + boxString + ", required:" + required
                        + " \n in parameter #" + (p + 1)
                        + " for method "
                        + method.getDeclaringClass().getSimpleName()
                        + "."
                        + method.getName()
                );
            }
        }

        private static boolean required(@Nullable Annotation[] annotations) {
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Required) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Nullable
        private static Box findBoxAnnotation(@Nullable Annotation[] annotations) {
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Box) {
                        return (Box) annotation;
                    }
                }
            }
            return null;
        }
    }

    static final class KeyRequired {
        final String key;
        final boolean required;

        KeyRequired(String key, boolean required) {
            this.key = key;
            this.required = required;
        }
    }
}
