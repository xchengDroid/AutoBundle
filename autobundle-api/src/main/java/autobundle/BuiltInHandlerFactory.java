package autobundle;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Add the built-in parameterHandler factory first. This prevents overriding its behavior but also
 * ensures correct behavior when using parameterHandlers that consume all types.
 */
final class BuiltInHandlerFactory extends ParameterHandler.Factory {
    static final BuiltInHandlerFactory INSTANCE = new BuiltInHandlerFactory();

    @Nullable
    @Override
    public ParameterHandler<?> get(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations) {
        //基础数据类型
        if (type == int.class) {
            return new ParameterHandler<Integer>() {
                @Override
                public void apply(Bundle bundle, String key, Integer value, boolean required) {
                    bundle.putInt(key, value);
                }
            };
        } else if (type == long.class) {
            return new ParameterHandler<Long>() {
                @Override
                public void apply(Bundle bundle, String key, Long value, boolean required) {
                    bundle.putLong(key, value);
                }
            };
        } else if (type == double.class) {
            return new ParameterHandler<Double>() {
                @Override
                public void apply(Bundle bundle, String key, Double value, boolean required) {
                    bundle.putDouble(key, value);
                }
            };
        } else if (type == float.class) {
            return new ParameterHandler<Float>() {
                @Override
                public void apply(Bundle bundle, String key, Float value, boolean required) {
                    bundle.putFloat(key, value);
                }
            };
        } else if (type == byte.class) {
            return new ParameterHandler<Byte>() {
                @Override
                public void apply(Bundle bundle, String key, Byte value, boolean required) {
                    bundle.putByte(key, value);
                }
            };
        } else if (type == Short.class) {
            return new ParameterHandler<Short>() {
                @Override
                public void apply(Bundle bundle, String key, Short value, boolean required) {
                    bundle.putShort(key, value);
                }
            };
        } else if (type == char.class) {
            return new ParameterHandler<Character>() {
                @Override
                public void apply(Bundle bundle, String key, Character value, boolean required) {
                    bundle.putChar(key, value);
                }
            };
        } else if (type == boolean.class) {
            return new ParameterHandler<Boolean>() {
                @Override
                public void apply(Bundle bundle, String key, Boolean value, boolean required) {
                    bundle.putBoolean(key, value);
                }
            };
        }

        //基础数据类型数组
        if (type == boolean[].class) {
            return new ParameterHandler<boolean[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable boolean[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putBooleanArray(key, value);
                }
            };
        } else if (type == byte[].class) {
            return new ParameterHandler<byte[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable byte[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putByteArray(key, value);
                }
            };
        } else if (type == char[].class) {
            return new ParameterHandler<char[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable char[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putCharArray(key, value);
                }
            };
        } else if (type == double[].class) {
            return new ParameterHandler<double[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable double[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putDoubleArray(key, value);
                }
            };
        } else if (type == float[].class) {
            return new ParameterHandler<float[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable float[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putFloatArray(key, value);
                }
            };
        } else if (type == int[].class) {
            return new ParameterHandler<int[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable int[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putIntArray(key, value);
                }
            };
        } else if (type == long[].class) {
            return new ParameterHandler<long[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable long[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putLongArray(key, value);
                }
            };
        } else if (type == short[].class) {
            return new ParameterHandler<short[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable short[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putShortArray(key, value);
                }
            };
        }
        //String数据类型和数组
        if (type == String.class) {
            return new ParameterHandler<String>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable String value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putString(key, value);
                }
            };
        } else if (type == String[].class) {
            return new ParameterHandler<String[]>() {
                @Override
                public void apply(Bundle bundle, String key, @Nullable String[] value, boolean required) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putStringArray(key, value);
                }
            };
        }
        
        if (type instanceof ParameterizedType) {
            Class<?> rawType = getRawType(type);
            Type elementType = getParameterUpperBound(0, (ParameterizedType) type);

            //因为String 和 Integer 都是final,确定是可以安全传入的
            //而 Parcelable 和 CharSequence 可以由子类实现，具有一定的不确定性，Bundle写入Parcel是否会奔溃
            if (rawType == ArrayList.class) {
                if (elementType == String.class) {
                    return new ParameterHandler<ArrayList<String>>() {
                        @Override
                        public void apply(Bundle bundle, String key, @Nullable ArrayList<String> value, boolean required) {
                            Utils.checkRequiredValue(key, value, required);
                            bundle.putStringArrayList(key, value);
                        }
                    };
                } else if (elementType == Integer.class) {
                    return new ParameterHandler<ArrayList<Integer>>() {
                        @Override
                        public void apply(Bundle bundle, String key, @Nullable ArrayList<Integer> value, boolean required) {
                            Utils.checkRequiredValue(key, value, required);
                            bundle.putIntegerArrayList(key, value);
                        }
                    };
                }
            }
        }
        return null;
    }
}