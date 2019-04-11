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
import autobundle.annotation.Required;
import autobundle.annotation.SerializableValue;
import autobundle.annotation.ShortArrayValue;
import autobundle.annotation.ShortValue;
import autobundle.annotation.SparseParcelableArrayValue;
import autobundle.annotation.StringArrayListValue;
import autobundle.annotation.StringArrayValue;
import autobundle.annotation.StringValue;

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

    private BundleFactory(ParameterHandler<?>[] parameterHandlers) {
        this.parameterHandlers = parameterHandlers;
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
            handlers[p].apply(bundle, args[p]);
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

        Builder(Method method) {
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterTypes = method.getGenericParameterTypes();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        BundleFactory build() {
            int parameterCount = parameterAnnotationsArray.length;
            parameterHandlers = new ParameterHandler<?>[parameterCount];
            for (int p = 0; p < parameterCount; p++) {
                parameterHandlers[p] = parseParameter(p, parameterTypes[p], parameterAnnotationsArray[p]);
            }
            return new BundleFactory(parameterHandlers);
        }

        private ParameterHandler<?> parseParameter(
                int p, Type parameterType, @Nullable Annotation[] annotations) {
            ParameterHandler<?> result = null;
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    ParameterHandler<?> annotationAction =
                            parseParameterAnnotation(p, parameterType, annotation, required(annotations));
                    if (annotationAction == null) {
                        continue;
                    }

                    if (result != null) {
                        throw parameterError(method, p,
                                "Multiple AutoBundle annotations found, only one allowed.");
                    }

                    result = annotationAction;
                }
            }

            if (result == null) {
                throw parameterError(method, p, "No AutoBundle annotation found.");
            }

            return result;
        }

        @Nullable
        private ParameterHandler<?> parseParameterAnnotation(
                int p, Type type, Annotation annotation, boolean required) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();

            if (AutoBundle.getInstance().debug) {
                Log.d(AutoBundle.TAG, "Parse " + annotation + " \nin parameter #" + (p + 1)
                        + " for method "
                        + method.getDeclaringClass().getSimpleName()
                        + "."
                        + method.getName()
                );
            }
            // 基础数据类型
            if (annotation instanceof IntValue) {
                checkParameterType(annotationClass, type, int.class, p);
                return ParameterHandler.create(annotationClass, ((IntValue) annotation).value(), required);
            } else if (annotation instanceof LongValue) {
                checkParameterType(annotationClass, type, long.class, p);
                return ParameterHandler.create(annotationClass, ((LongValue) annotation).value(), required);
            } else if (annotation instanceof DoubleValue) {
                checkParameterType(annotationClass, type, double.class, p);
                return ParameterHandler.create(annotationClass, ((DoubleValue) annotation).value(), required);
            } else if (annotation instanceof FloatValue) {
                checkParameterType(annotationClass, type, float.class, p);
                return ParameterHandler.create(annotationClass, ((FloatValue) annotation).value(), required);
            } else if (annotation instanceof ByteValue) {
                checkParameterType(annotationClass, type, byte.class, p);
                return ParameterHandler.create(annotationClass, ((ByteValue) annotation).value(), required);
            } else if (annotation instanceof ShortValue) {
                checkParameterType(annotationClass, type, short.class, p);
                return ParameterHandler.create(annotationClass, ((ShortValue) annotation).value(), required);
            } else if (annotation instanceof CharValue) {
                checkParameterType(annotationClass, type, char.class, p);
                return ParameterHandler.create(annotationClass, ((CharValue) annotation).value(), required);
            } else if (annotation instanceof BooleanValue) {
                checkParameterType(annotationClass, type, boolean.class, p);
                return ParameterHandler.create(annotationClass, ((BooleanValue) annotation).value(), required);
            }

            // 复合数据类型
            else if (annotation instanceof StringValue) {
                checkParameterType(annotationClass, type, String.class, p);
                return ParameterHandler.create(annotationClass, ((StringValue) annotation).value(), required);
            } else if (annotation instanceof CharSequenceValue) {
                checkParameterType(annotationClass, type, CharSequence.class, p);
                return ParameterHandler.create(annotationClass, ((CharSequenceValue) annotation).value(), required);
            } else if (annotation instanceof SerializableValue) {
                checkParameterType(annotationClass, type, Serializable.class, p);
                return ParameterHandler.create(annotationClass, ((SerializableValue) annotation).value(), required);
            } else if (annotation instanceof ParcelableValue) {
                checkParameterType(annotationClass, type, Parcelable.class, p);
                return ParameterHandler.create(annotationClass, ((ParcelableValue) annotation).value(), required);
            }

            // 基础数据类型数组
            else if (annotation instanceof BooleanArrayValue) {
                checkParameterType(annotationClass, type, boolean[].class, p);
                return ParameterHandler.create(annotationClass, ((BooleanArrayValue) annotation).value(), required);
            } else if (annotation instanceof ByteArrayValue) {
                checkParameterType(annotationClass, type, byte[].class, p);
                return ParameterHandler.create(annotationClass, ((ByteArrayValue) annotation).value(), required);
            } else if (annotation instanceof CharArrayValue) {
                checkParameterType(annotationClass, type, char[].class, p);
                return ParameterHandler.create(annotationClass, ((CharArrayValue) annotation).value(), required);
            } else if (annotation instanceof DoubleArrayValue) {
                checkParameterType(annotationClass, type, double[].class, p);
                return ParameterHandler.create(annotationClass, ((DoubleArrayValue) annotation).value(), required);
            } else if (annotation instanceof FloatArrayValue) {
                checkParameterType(annotationClass, type, float[].class, p);
                return ParameterHandler.create(annotationClass, ((FloatArrayValue) annotation).value(), required);
            } else if (annotation instanceof IntArrayValue) {
                checkParameterType(annotationClass, type, int[].class, p);
                return ParameterHandler.create(annotationClass, ((IntArrayValue) annotation).value(), required);
            } else if (annotation instanceof LongArrayValue) {
                checkParameterType(annotationClass, type, long[].class, p);
                return ParameterHandler.create(annotationClass, ((LongArrayValue) annotation).value(), required);
            } else if (annotation instanceof ShortArrayValue) {
                checkParameterType(annotationClass, type, short[].class, p);
                return ParameterHandler.create(annotationClass, ((ShortArrayValue) annotation).value(), required);
            }

            // 复合数据类型数组
            else if (annotation instanceof ParcelableArrayValue) {
                // Parcelable[] value
                checkParameterType(annotationClass, type, Parcelable[].class, p);
                return ParameterHandler.create(annotationClass, ((ParcelableArrayValue) annotation).value(), required);
            } else if (annotation instanceof CharSequenceArrayValue) {
                // CharSequence[] value
                checkParameterType(annotationClass, type, CharSequence[].class, p);
                return ParameterHandler.create(annotationClass, ((CharSequenceArrayValue) annotation).value(), required);
            } else if (annotation instanceof StringArrayValue) {
                //String[] value
                checkParameterType(annotationClass, type, String[].class, p);
                return ParameterHandler.create(annotationClass, ((StringArrayValue) annotation).value(), required);
            } else if (annotation instanceof SparseParcelableArrayValue) {
                //SparseArray<? extends Parcelable>
                checkParameterType(annotationClass, type, SparseArray.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, Parcelable.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> listItemClass = Utils.getRawType(Utils.getParameterUpperBound(0, parameterizedType));
                //只要任意实现Parcelable接口的子类都可以
                if (!Parcelable.class.isAssignableFrom(listItemClass)) {
                    arrayListTypeError(rawParameterType, Parcelable.class, p);
                }
                return ParameterHandler.create(annotationClass, ((SparseParcelableArrayValue) annotation).value(), required);
            }
            //列表类型ArrayList
            else if (annotation instanceof CharSequenceArrayListValue) {
                // ArrayList<CharSequence> value
                checkParameterType(annotationClass, type, ArrayList.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, CharSequence.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type listItemType = Utils.getParameterUpperBound(0, parameterizedType);

                //note:    if  -> ArrayList<String> stringList = new ArrayList<>();
                //not allowed  -> bundle.putCharSequenceArrayList("strings",stringList);
                //so must use  == ;can not use isAssignableFrom()
                if (CharSequence.class != listItemType) {
                    arrayListTypeError(rawParameterType, CharSequence.class, p);
                }
                return ParameterHandler.create(annotationClass, ((CharSequenceArrayListValue) annotation).value(), required);
            } else if (annotation instanceof IntegerArrayListValue) {
                // ArrayList<Integer> value
                checkParameterType(annotationClass, type, ArrayList.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, Integer.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type listItemType = Utils.getParameterUpperBound(0, parameterizedType);
                if (Integer.class != listItemType) {
                    arrayListTypeError(rawParameterType, Integer.class, p);
                }
                return ParameterHandler.create(annotationClass, ((IntegerArrayListValue) annotation).value(), required);
            } else if (annotation instanceof ParcelableArrayListValue) {
                //ArrayList<? extends Parcelable> value
                checkParameterType(annotationClass, type, ArrayList.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, Parcelable.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> listItemClass = Utils.getRawType(Utils.getParameterUpperBound(0, parameterizedType));
                //只要任意实现Parcelable接口的子类都可以
                if (!Parcelable.class.isAssignableFrom(listItemClass)) {
                    arrayListTypeError(rawParameterType, Parcelable.class, p);
                }
                return ParameterHandler.create(annotationClass, ((ParcelableArrayListValue) annotation).value(), required);
            } else if (annotation instanceof StringArrayListValue) {
                //ArrayList<String> value
                checkParameterType(annotationClass, type, ArrayList.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, String.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type listItemType = Utils.getParameterUpperBound(0, parameterizedType);
                if (String.class != listItemType) {
                    arrayListTypeError(rawParameterType, String.class, p);
                }
                return ParameterHandler.create(annotationClass, ((StringArrayListValue) annotation).value(), required);
            }
            return null;
        }


        private void checkParameterType(Class<? extends Annotation> annotationClass, Type type, Class<?> rightClass, int p) {
            Class<?> rawType = Utils.getRawType(type);
            if (!rightClass.isAssignableFrom(rawType)) {
                // annotation.getClass -->Proxy 动态代理
                throw parameterError(method, p,
                        "@" + annotationClass.getSimpleName() + " must be " + rightClass.getSimpleName() + " type.");
            }
        }

        private void arrayListTypeError(Class<?> rawParameterType, Class<?> rightItemClass, int p) {
            throw parameterError(method, p, rawParameterType.getSimpleName()
                    + " must include generic type (e.g., "
                    + rawParameterType.getSimpleName()
                    + "<" + rightItemClass.getSimpleName() + ">)");

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
    }
}
