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
            // 基础数据类型
            if (annotation instanceof IntValue) {
                checkParameterType(annotation, type, int.class, p);
                return new ParameterHandler.IntParameterHandler(((IntValue) annotation).value(), required);
            } else if (annotation instanceof LongValue) {
                checkParameterType(annotation, type, long.class, p);
                return new ParameterHandler.IntParameterHandler(((LongValue) annotation).value(), required);
            } else if (annotation instanceof DoubleValue) {
                checkParameterType(annotation, type, double.class, p);
                return new ParameterHandler.IntParameterHandler(((DoubleValue) annotation).value(), required);
            } else if (annotation instanceof FloatValue) {
                checkParameterType(annotation, type, float.class, p);
                return new ParameterHandler.IntParameterHandler(((FloatValue) annotation).value(), required);
            } else if (annotation instanceof ByteValue) {
                checkParameterType(annotation, type, byte.class, p);
                return new ParameterHandler.IntParameterHandler(((ByteValue) annotation).value(), required);
            } else if (annotation instanceof ShortValue) {
                checkParameterType(annotation, type, short.class, p);
                return new ParameterHandler.IntParameterHandler(((ShortValue) annotation).value(), required);
            } else if (annotation instanceof CharValue) {
                checkParameterType(annotation, type, char.class, p);
                return new ParameterHandler.IntParameterHandler(((CharValue) annotation).value(), required);
            } else if (annotation instanceof BooleanValue) {
                checkParameterType(annotation, type, boolean.class, p);
                return new ParameterHandler.IntParameterHandler(((BooleanValue) annotation).value(), required);
            }


            // 基础数据类型数组
            else if (annotation instanceof BooleanArrayValue) {
                checkParameterType(annotation, type, boolean[].class, p);
                return new ParameterHandler.IntParameterHandler(((BooleanArrayValue) annotation).value(), required);
            } else if (annotation instanceof ByteArrayValue) {
                checkParameterType(annotation, type, byte[].class, p);
                return new ParameterHandler.IntParameterHandler(((ByteArrayValue) annotation).value(), required);
            } else if (annotation instanceof CharArrayValue) {
                checkParameterType(annotation, type, char[].class, p);
                return new ParameterHandler.IntParameterHandler(((CharArrayValue) annotation).value(), required);
            } else if (annotation instanceof DoubleArrayValue) {
                checkParameterType(annotation, type, double[].class, p);
                return new ParameterHandler.IntParameterHandler(((DoubleArrayValue) annotation).value(), required);
            } else if (annotation instanceof FloatArrayValue) {
                checkParameterType(annotation, type, float[].class, p);
                return new ParameterHandler.IntParameterHandler(((FloatArrayValue) annotation).value(), required);
            } else if (annotation instanceof IntArrayValue) {
                checkParameterType(annotation, type, int[].class, p);
                return new ParameterHandler.IntParameterHandler(((IntArrayValue) annotation).value(), required);
            } else if (annotation instanceof LongArrayValue) {
                checkParameterType(annotation, type, long[].class, p);
                return new ParameterHandler.IntParameterHandler(((LongArrayValue) annotation).value(), required);
            } else if (annotation instanceof ShortArrayValue) {
                checkParameterType(annotation, type, short[].class, p);
                return new ParameterHandler.IntParameterHandler(((ShortArrayValue) annotation).value(), required);
            }

            // 复合数据类型数组
            else if (annotation instanceof ParcelableArrayValue) {
                checkParameterType(annotation, type, Parcelable[].class, p);
                return new ParameterHandler.IntParameterHandler(((ParcelableArrayValue) annotation).value(), required);
            } else if (annotation instanceof CharSequenceArrayValue) {
                checkParameterType(annotation, type, CharSequence[].class, p);
                return new ParameterHandler.IntParameterHandler(((CharSequenceArrayValue) annotation).value(), required);
            } else if (annotation instanceof StringArrayValue) {
                checkParameterType(annotation, type, String[].class, p);
                return new ParameterHandler.IntParameterHandler(((StringArrayValue) annotation).value(), required);
            } else if (annotation instanceof SparseParcelableArrayValue) {
                //SparseArray<? extends Parcelable>
                checkParameterType(annotation, type, SparseArray.class, p);
                return new ParameterHandler.IntParameterHandler(((SparseParcelableArrayValue) annotation).value(), required);
            }
            //列表类型ArrayList
            else if (annotation instanceof CharSequenceArrayListValue) {
                // ArrayList<CharSequence> value
                checkParameterType(annotation, type, ArrayList.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, CharSequence.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type listItemType = Utils.getParameterUpperBound(0, parameterizedType);
                if (CharSequence.class != listItemType) {
                    arrayListTypeError(rawParameterType, CharSequence.class, p);
                }
                return new ParameterHandler.IntParameterHandler(((CharSequenceArrayListValue) annotation).value(), required);
            } else if (annotation instanceof IntegerArrayListValue) {
                // ArrayList<Integer> value
                checkParameterType(annotation, type, ArrayList.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, Integer.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type listItemType = Utils.getParameterUpperBound(0, parameterizedType);
                if (Integer.class != listItemType) {
                    arrayListTypeError(rawParameterType, Integer.class, p);
                }
                return new ParameterHandler.IntParameterHandler(((IntegerArrayListValue) annotation).value(), required);
            } else if (annotation instanceof ParcelableArrayListValue) {
                //ArrayList<? extends Parcelable> value
                checkParameterType(annotation, type, ArrayList.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, Parcelable.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> listItemClass = Utils.getRawType(Utils.getParameterUpperBound(0, parameterizedType));
                //只要任意实现Parcelable接口的子类都可以
                if (Parcelable.class.isAssignableFrom(listItemClass)) {
                    arrayListTypeError(rawParameterType, Parcelable.class, p);
                }
                return new ParameterHandler.IntParameterHandler(((ParcelableArrayListValue) annotation).value(), required);
            } else if (annotation instanceof StringArrayListValue) {
                //ArrayList<String> value
                checkParameterType(annotation, type, ArrayList.class, p);
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!(type instanceof ParameterizedType)) {
                    arrayListTypeError(rawParameterType, String.class, p);
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type listItemType = Utils.getParameterUpperBound(0, parameterizedType);
                if (String.class != listItemType) {
                    arrayListTypeError(rawParameterType, String.class, p);
                }
                return new ParameterHandler.IntParameterHandler(((StringArrayListValue) annotation).value(), required);
            }


            // 复合数据类型
            else if (annotation instanceof StringValue) {
                checkParameterType(annotation, type, String.class, p);
                return new ParameterHandler.IntParameterHandler(((StringValue) annotation).value(), required);
            } else if (annotation instanceof CharSequenceValue) {
                checkParameterType(annotation, type, CharSequence.class, p);
                return new ParameterHandler.IntParameterHandler(((CharSequenceValue) annotation).value(), required);
            } else if (annotation instanceof SerializableValue) {
                checkParameterType(annotation, type, Serializable.class, p);
                return new ParameterHandler.IntParameterHandler(((SerializableValue) annotation).value(), required);
            } else if (annotation instanceof ParcelableValue) {
                checkParameterType(annotation, type, Parcelable.class, p);
                return new ParameterHandler.IntParameterHandler(((ParcelableValue) annotation).value(), required);
            }
            return null;
        }


        private void checkParameterType(Annotation annotation, Type type, Class<?> rightClass, int p) {
            Class<?> rawType = Utils.getRawType(type);
            if (!rightClass.isAssignableFrom(rawType)) {
                // annotation.getClass -->Proxy 动态代理
                throw parameterError(method, p,
                        "@" + annotation.annotationType().getSimpleName() + " must be " + rightClass.getSimpleName() + " type.");
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
