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
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import autobundle.annotation.BooleanValue;
import autobundle.annotation.CharSequenceValue;
import autobundle.annotation.IntValue;
import autobundle.annotation.Required;
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

    static BundleFactory parseAnnotations(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType != Bundle.class) {
            throw Utils.methodError(method, "Service methods must return Bundle.");
        }
        return new Builder(method).build();
    }

    private final Method method;
    private final ParameterHandler<?>[] parameterHandlers;

    private BundleFactory(Builder builder) {
        method = builder.method;
        parameterHandlers = builder.parameterHandlers;
    }

    Bundle create(Object[] args) {
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
            return new BundleFactory(this);
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
            if (annotation instanceof IntValue) {
                if (type != int.class) {
                    throw parameterError(method, p,
                            "@IntValue must be int type.");
                }
                return new ParameterHandler.IntParameterHandler(((IntValue) annotation).value(), required);
            } else if (annotation instanceof BooleanValue) {
                if (type != boolean.class) {
                    throw parameterError(method, p,
                            "@BooleanValue must be boolean type.");
                }
                return new ParameterHandler.IntParameterHandler(((BooleanValue) annotation).value(), required);
            } else if (annotation instanceof StringValue) {
                if (type != String.class) {
                    throw parameterError(method, p,
                            "@StringValue must be String type.");
                }
                return new ParameterHandler.IntParameterHandler(((StringValue) annotation).value(), required);
            } else if (annotation instanceof CharSequenceValue) {
                if (type != CharSequenceValue.class) {
                    throw parameterError(method, p,
                            "@CharSequenceValue must be CharSequence type.");
                }
                return new ParameterHandler.IntParameterHandler(((CharSequenceValue) annotation).value(), required);
            }
            return null;
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
