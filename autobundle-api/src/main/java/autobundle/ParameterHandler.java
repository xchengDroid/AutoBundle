package autobundle;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

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

abstract class ParameterHandler<T> {
    final String key;
    //Primitive type wont be check!
    final boolean required;

    private ParameterHandler(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    abstract void apply(Bundle bundle, @Nullable T value);

    static ParameterHandler<?> create(Class<? extends Annotation> annotationClass, String key,
                                      boolean required) {
        //基础数据类型
        if (annotationClass == IntValue.class) {
            return new ParameterHandler<Integer>(key, required) {
                @Override
                void apply(Bundle bundle, Integer value) {
                    bundle.putInt(key, value);
                }
            };
        } else if (annotationClass == LongValue.class) {
            return new ParameterHandler<Long>(key, required) {
                @Override
                void apply(Bundle bundle, Long value) {
                    bundle.putLong(key, value);
                }
            };
        } else if (annotationClass == DoubleValue.class) {
            return new ParameterHandler<Double>(key, required) {
                @Override
                void apply(Bundle bundle, Double value) {
                    bundle.putDouble(key, value);
                }
            };
        } else if (annotationClass == FloatValue.class) {
            return new ParameterHandler<Float>(key, required) {
                @Override
                void apply(Bundle bundle, Float value) {
                    bundle.putFloat(key, value);
                }
            };
        } else if (annotationClass == ByteValue.class) {
            return new ParameterHandler<Byte>(key, required) {
                @Override
                void apply(Bundle bundle, Byte value) {
                    bundle.putByte(key, value);
                }
            };
        } else if (annotationClass == ShortValue.class) {
            return new ParameterHandler<Short>(key, required) {
                @Override
                void apply(Bundle bundle, Short value) {
                    bundle.putShort(key, value);
                }
            };
        } else if (annotationClass == CharValue.class) {
            return new ParameterHandler<Character>(key, required) {
                @Override
                void apply(Bundle bundle, Character value) {
                    bundle.putChar(key, value);
                }
            };
        } else if (annotationClass == BooleanValue.class) {
            return new ParameterHandler<Boolean>(key, required) {
                @Override
                void apply(Bundle bundle, Boolean value) {
                    bundle.putBoolean(key, value);
                }
            };
        }

        //复合数据类型
        else if (annotationClass == StringValue.class) {
            return new ParameterHandler<String>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable String value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putString(key, value);
                }
            };
        } else if (annotationClass == CharSequenceValue.class) {
            return new ParameterHandler<CharSequence>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable CharSequence value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putCharSequence(key, value);
                }
            };
        } else if (annotationClass == SerializableValue.class) {
            return new ParameterHandler<Serializable>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable Serializable value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putSerializable(key, value);
                }
            };
        } else if (annotationClass == ParcelableValue.class) {
            return new ParameterHandler<Parcelable>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable Parcelable value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putParcelable(key, value);
                }
            };
        }

        //基础数据类型数组
        else if (annotationClass == BooleanArrayValue.class) {
            return new ParameterHandler<boolean[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable boolean[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putBooleanArray(key, value);
                }
            };
        } else if (annotationClass == ByteArrayValue.class) {
            return new ParameterHandler<byte[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable byte[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putByteArray(key, value);
                }
            };
        } else if (annotationClass == CharArrayValue.class) {
            return new ParameterHandler<char[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable char[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putCharArray(key, value);
                }
            };
        } else if (annotationClass == DoubleArrayValue.class) {
            return new ParameterHandler<double[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable double[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putDoubleArray(key, value);
                }
            };
        } else if (annotationClass == FloatArrayValue.class) {
            return new ParameterHandler<float[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable float[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putFloatArray(key, value);
                }
            };
        } else if (annotationClass == IntArrayValue.class) {
            return new ParameterHandler<int[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable int[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putIntArray(key, value);
                }
            };
        } else if (annotationClass == LongArrayValue.class) {
            return new ParameterHandler<long[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable long[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putLongArray(key, value);
                }
            };
        } else if (annotationClass == ShortArrayValue.class) {
            return new ParameterHandler<short[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable short[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putShortArray(key, value);
                }
            };
        }

        //复合数据类型数组
        else if (annotationClass == ParcelableArrayValue.class) {
            return new ParameterHandler<Parcelable[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable Parcelable[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putParcelableArray(key, value);
                }
            };
        } else if (annotationClass == CharSequenceArrayValue.class) {
            return new ParameterHandler<CharSequence[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable CharSequence[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putCharSequenceArray(key, value);
                }
            };
        } else if (annotationClass == StringArrayValue.class) {
            return new ParameterHandler<String[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable String[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putStringArray(key, value);
                }
            };
        } else if (annotationClass == SparseParcelableArrayValue.class) {
            return new ParameterHandler<SparseArray<? extends Parcelable>>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable SparseArray<? extends Parcelable> value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putSparseParcelableArray(key, value);
                }
            };
        }

        //列表类型ArrayList
        else if (annotationClass == CharSequenceArrayListValue.class) {
            return new ParameterHandler<ArrayList<CharSequence>>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable ArrayList<CharSequence> value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putCharSequenceArrayList(key, value);
                }
            };
        } else if (annotationClass == IntegerArrayListValue.class) {
            return new ParameterHandler<ArrayList<Integer>>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable ArrayList<Integer> value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putIntegerArrayList(key, value);
                }
            };
        } else if (annotationClass == ParcelableArrayListValue.class) {
            return new ParameterHandler<ArrayList<? extends Parcelable>>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable ArrayList<? extends Parcelable> value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putParcelableArrayList(key, value);
                }
            };
        } else if (annotationClass == StringArrayListValue.class) {
            return new ParameterHandler<ArrayList<String>>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable ArrayList<String> value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putStringArrayList(key, value);
                }
            };
        }
        throw new IllegalArgumentException("Argument (@" + annotationClass.getSimpleName()
                + ") doesn't matched ,please use AutoBundle annotation.");
    }
}
