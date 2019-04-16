/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package autobundle;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private BundleFactory(Method method, ParameterHandler<?>[] parameterHandlers, int bundleFlag) {
        this.method = method;
        this.parameterHandlers = parameterHandlers;
        this.bundleFlag = bundleFlag;
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
            handler.apply(bundle, args[p]);
            for (OnBundleListener listener : AutoBundle.getInstance().listeners) {
                listener.onBundling(bundleFlag, handler.key, args[p], handler.required);
            }
            if (AutoBundle.getInstance().debug) {
                Log.d(AutoBundle.TAG, "Bundling key: " + handler.key + ", value: " + args[p] + ", required: " + handler.required
                        + " \n in parameter #" + (p + 1)
                        + " for method "
                        + method.getDeclaringClass().getSimpleName()
                        + "."
                        + method.getName()
                );
            }
        }
        for (OnBundleListener listener : AutoBundle.getInstance().listeners) {
            listener.onCompleted(bundleFlag, bundle);
        }
        return bundle;
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
            for (int p = 0; p < parameterCount; p++) {
                parameterHandlers[p] = parseParameter(p, parameterTypes[p], parameterAnnotationsArray[p]);
            }
            return new BundleFactory(method, parameterHandlers, bundleFlag);
        }

        private ParameterHandler<?> parseParameter(
                int p, Type parameterType, @Nullable Annotation[] annotations) {
            // 同一个注解不能多次使用在参数上
            Box boxAnnotation = findBoxAnnotation(annotations);
            if (boxAnnotation == null) {
                throw parameterError(method, p, "@%s annotation not found.", Box.class.getSimpleName());
            }
            boolean required = required(annotations);
            if (AutoBundle.getInstance().debug) {
                Log.d(AutoBundle.TAG, "Parse " + boxAnnotation + " ,required:" + required
                        + " \n in parameter #" + (p + 1)
                        + " for method "
                        + method.getDeclaringClass().getSimpleName()
                        + "."
                        + method.getName()
                );
            }
            return parseParameterAnnotation(p, parameterType, boxAnnotation.value(), required(annotations));
        }

        /**
         * 静态方法防止匿名内部类持有Builder引用
         * Serializable.class.isAssignableFrom(AnyClass[].class)==true
         */
        @NonNull
        private ParameterHandler<?> parseParameterAnnotation(int p, Type type, String key, boolean required) {
            //不带泛型
            if (type instanceof Class<?>) {
                Class<?> clazz = (Class<?>) type;
                if (clazz.isPrimitive()) {
                    //basic ->8 methods
                    return ParameterHandler.getSerializable(key, required);
                } else if (clazz.isArray()) {
                    // Must be before Serializable
                    //任意类型的数组都是Serializable 的子类,所以需要检测元素是否可以序列化
                    Class<?> elementClass = getComponentType(clazz);
                    if (elementClass.isPrimitive() ||
                            Serializable.class.isAssignableFrom(elementClass)) {
                        //basicArray ->8 methods
                        //putStringArray
                        return ParameterHandler.getSerializable(key, required);
                    } else if (Parcelable.class.isAssignableFrom(elementClass)) {
                        //putParcelableArray
                        return ParameterHandler.getParcelableArray(key, required);
                    } else if (CharSequence.class.isAssignableFrom(elementClass)) {
                        //putCharSequenceArray
                        return ParameterHandler.getCharSequenceArray(key, required);
                    }
                } else if (ArrayList.class.isAssignableFrom(clazz)) {
                    arrayListTypeError(clazz, p);
                } else if (SparseArray.class.isAssignableFrom(clazz)) {
                    sparseArrayTypeError(clazz, p);
                } else if (Parcelable.class.isAssignableFrom(clazz)) {
                    //putParcelable
                    return ParameterHandler.getParcelable(key, required);
                } else if (Serializable.class.isAssignableFrom(clazz)) {
                    // Must be after Array include bundle.putString
                    //putSerializable
                    //putString
                    return ParameterHandler.getSerializable(key, required);
                } else if (CharSequence.class.isAssignableFrom(clazz)) {
                    //putCharSequence
                    return ParameterHandler.getCharSequence(key, required);
                }
            } else if (type instanceof ParameterizedType) {
                Class<?> rawType = Utils.getRawType(type);
                //只检测第一个泛型
                Class<?> elementClass = Utils.getRawType(Utils.getParameterUpperBound(0, (ParameterizedType) type));
                if (ArrayList.class.isAssignableFrom(rawType)) {
                    if (Parcelable.class.isAssignableFrom(elementClass)) {
                        //putParcelableArrayList
                        return ParameterHandler.getParcelableArrayList(key, required);
                    } else if (Serializable.class.isAssignableFrom(elementClass)) {
                        //putStringArrayList
                        //putIntegerArrayList
                        return ParameterHandler.getSerializable(key, required);
                    } else if (CharSequence.class == elementClass) {
                        //note:    if  -> ArrayList<String> stringList = new ArrayList<>();
                        //not allowed  -> bundle.putCharSequenceArrayList("strings",stringList);
                        //so must use  == ;can not use isAssignableFrom()
                        //putCharSequenceArrayList
                        return ParameterHandler.getCharSequenceArrayList(key, required);
                    }
                    arrayListTypeError(rawType, p);
                } else if (SparseArray.class.isAssignableFrom(rawType)) {
                    if (Parcelable.class.isAssignableFrom(elementClass)) {
                        //putSparseParcelableArray
                        return ParameterHandler.getSparseParcelableArray(key, required);
                    }
                    sparseArrayTypeError(rawType, p);
                }
            }
            throw parameterError(method, p, "'" + type
                    + "' doesn't support.");
        }

        /**
         * check {@link Bundle#putStringArrayList(String, ArrayList)}
         * check {@link Bundle#putIntegerArrayList(String, ArrayList)}
         * check {@link Bundle#putCharSequenceArrayList(String, ArrayList)}
         * check {@link Bundle#putSparseParcelableArray(String, SparseArray)}
         */
        private void arrayListTypeError(Class<?> rawParameterType, int p) {
            StringBuilder typeString = new StringBuilder();
            String rawTypeName = rawParameterType.getSimpleName();

            typeString.append(rawTypeName).append("<String>, ");
            typeString.append(rawTypeName).append("<Integer>, ");
            typeString.append(rawTypeName).append("<CharSequence>, ");
            typeString.append(rawTypeName).append("<? extends Parcelable>, ");
            typeString.append(rawTypeName).append("<? extends Serializable>");

            throw parameterError(method, p, rawParameterType.getSimpleName()
                    + " must include generic type \n(e.g., "
                    + typeString.toString()
                    + ")");
        }

        /**
         * check {@link Bundle#putSparseParcelableArray(String, SparseArray)}
         */
        private void sparseArrayTypeError(Class<?> rawParameterType, int p) {
            StringBuilder typeString = new StringBuilder();
            String rawTypeName = rawParameterType.getSimpleName();
            typeString.append(rawTypeName).append("<? extends Parcelable>");
            throw parameterError(method, p, rawParameterType.getSimpleName()
                    + " must include generic type (e.g., "
                    + typeString.toString()
                    + ")");
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

        static Class<?> getComponentType(Class<?> clazz) {
            if (clazz.isArray()) {
                clazz = clazz.getComponentType();
                Utils.checkNotNull(clazz, "clazz==null");
                return getComponentType(clazz);
            }
            return clazz;
        }
    }
}
