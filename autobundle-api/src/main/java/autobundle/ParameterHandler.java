package autobundle;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 创建时间：2019/4/11
 * 编写人： chengxin
 * 功能描述：called bundle.putXXX method
 */
abstract class ParameterHandler<T> {
    final String key;
    //Primitive type wont be check!
    final boolean required;

    ParameterHandler(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    abstract void apply(Bundle bundle, @Nullable T value);

    static ParameterHandler<?> getBasic(Class<?> clazz, String key, boolean required) {
        //基础数据类型
        if (clazz == int.class) {
            return new ParameterHandler<Integer>(key, required) {
                @Override
                void apply(Bundle bundle, Integer value) {
                    bundle.putInt(key, value);
                }
            };
        } else if (clazz == long.class) {
            return new ParameterHandler<Long>(key, required) {
                @Override
                void apply(Bundle bundle, Long value) {
                    bundle.putLong(key, value);
                }
            };
        } else if (clazz == double.class) {
            return new ParameterHandler<Double>(key, required) {
                @Override
                void apply(Bundle bundle, Double value) {
                    bundle.putDouble(key, value);
                }
            };
        } else if (clazz == float.class) {
            return new ParameterHandler<Float>(key, required) {
                @Override
                void apply(Bundle bundle, Float value) {
                    bundle.putFloat(key, value);
                }
            };
        } else if (clazz == byte.class) {
            return new ParameterHandler<Byte>(key, required) {
                @Override
                void apply(Bundle bundle, Byte value) {
                    bundle.putByte(key, value);
                }
            };
        } else if (clazz == short.class) {
            return new ParameterHandler<Short>(key, required) {
                @Override
                void apply(Bundle bundle, Short value) {
                    bundle.putShort(key, value);
                }
            };
        } else if (clazz == char.class) {
            return new ParameterHandler<Character>(key, required) {
                @Override
                void apply(Bundle bundle, Character value) {
                    bundle.putChar(key, value);
                }
            };
        } else if (clazz == boolean.class) {
            return new ParameterHandler<Boolean>(key, required) {
                @Override
                void apply(Bundle bundle, Boolean value) {
                    bundle.putBoolean(key, value);
                }
            };
        }
        throw Utils.typeUnsupported(clazz);
    }

    static ParameterHandler<?> getBasicArray(Class<?> clazz, String key, boolean required) {
        //基础数据类型数组
        if (clazz == boolean[].class) {
            return new ParameterHandler<boolean[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable boolean[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putBooleanArray(key, value);
                }
            };
        } else if (clazz == byte[].class) {
            return new ParameterHandler<byte[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable byte[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putByteArray(key, value);
                }
            };
        } else if (clazz == char[].class) {
            return new ParameterHandler<char[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable char[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putCharArray(key, value);
                }
            };
        } else if (clazz == double[].class) {
            return new ParameterHandler<double[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable double[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putDoubleArray(key, value);
                }
            };
        } else if (clazz == float[].class) {
            return new ParameterHandler<float[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable float[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putFloatArray(key, value);
                }
            };
        } else if (clazz == int[].class) {
            return new ParameterHandler<int[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable int[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putIntArray(key, value);
                }
            };
        } else if (clazz == long[].class) {
            return new ParameterHandler<long[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable long[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putLongArray(key, value);
                }
            };
        } else if (clazz == short[].class) {
            return new ParameterHandler<short[]>(key, required) {
                @Override
                void apply(Bundle bundle, @Nullable short[] value) {
                    Utils.checkRequiredValue(key, value, required);
                    bundle.putShortArray(key, value);
                }
            };
        }
        throw Utils.typeUnsupported(clazz);
    }

    static ParameterHandler<ArrayList<Integer>> getIntegerArrayList(String key, boolean required) {
        return new ParameterHandler<ArrayList<Integer>>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable ArrayList<Integer> value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putIntegerArrayList(key, value);
            }
        };
    }


    static ParameterHandler<Serializable> getSerializable(String key, boolean required) {
        return new ParameterHandler<Serializable>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable Serializable value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putSerializable(key, value);
            }
        };
    }

    static ParameterHandler<CharSequence> getCharSequence(String key, boolean required) {
        return new ParameterHandler<CharSequence>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable CharSequence value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putCharSequence(key, value);
            }
        };
    }

    static ParameterHandler<CharSequence[]> getCharSequenceArray(String key, boolean required) {
        return new ParameterHandler<CharSequence[]>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable CharSequence[] value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putCharSequenceArray(key, value);
            }
        };
    }

    static ParameterHandler<ArrayList<CharSequence>> getCharSequenceArrayList(String key, boolean required) {
        return new ParameterHandler<ArrayList<CharSequence>>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable ArrayList<CharSequence> value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putCharSequenceArrayList(key, value);
            }
        };
    }

    static ParameterHandler<String> getString(String key, boolean required) {
        return new ParameterHandler<String>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable String value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putString(key, value);
            }
        };
    }

    static ParameterHandler<String[]> getStringArray(String key, boolean required) {
        return new ParameterHandler<String[]>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable String[] value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putStringArray(key, value);
            }
        };
    }

    static ParameterHandler<ArrayList<String>> getStringArrayList(String key, boolean required) {
        return new ParameterHandler<ArrayList<String>>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable ArrayList<String> value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putStringArrayList(key, value);
            }
        };
    }


    static ParameterHandler<Parcelable> getParcelable(String key, boolean required) {
        return new ParameterHandler<Parcelable>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable Parcelable value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putParcelable(key, value);
            }
        };
    }

    static ParameterHandler<Parcelable[]> getParcelableArray(String key, boolean required) {
        return new ParameterHandler<Parcelable[]>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable Parcelable[] value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putParcelableArray(key, value);
            }
        };
    }

    static ParameterHandler<SparseArray<? extends Parcelable>> getSparseParcelableArray(String key, boolean required) {
        return new ParameterHandler<SparseArray<? extends Parcelable>>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable SparseArray<? extends Parcelable> value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putSparseParcelableArray(key, value);
            }
        };
    }

    static ParameterHandler<ArrayList<? extends Parcelable>> getParcelableArrayList(String key, boolean required) {
        return new ParameterHandler<ArrayList<? extends Parcelable>>(key, required) {
            @Override
            void apply(Bundle bundle, @Nullable ArrayList<? extends Parcelable> value) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putParcelableArrayList(key, value);
            }
        };
    }
}
