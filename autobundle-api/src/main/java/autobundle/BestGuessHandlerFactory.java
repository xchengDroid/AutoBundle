package autobundle;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 猜测数据类型
 */
final class BestGuessHandlerFactory extends ParameterHandler.Factory {
    @Nullable
    @Override
    public ParameterHandler<?> get(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isArray()) {
                Class<?> elementClass = clazz.getComponentType();
                assert elementClass != null;
                if (Parcelable.class.isAssignableFrom(elementClass)) {
                    return new ParameterHandler<Parcelable[]>() {
                        @Override
                        public void apply(Bundle bundle, String key, @Nullable Parcelable[] value, boolean required) {
                            Utils.checkRequiredValue(key, value, required);
                            bundle.putParcelableArray(key, value);
                        }
                    };
                } else if (CharSequence.class.isAssignableFrom(elementClass)) {
                    return new ParameterHandler<CharSequence[]>() {
                        @Override
                        public void apply(Bundle bundle, String key, @Nullable CharSequence[] value, boolean required) {
                            Utils.checkRequiredValue(key, value, required);
                            bundle.putCharSequenceArray(key, value);
                        }
                    };
                } else {
                    Class<?> outElementClass = getOutComponentType(clazz);
                    //任意类型的数组都是Serializable 的子类,所以需要检测元素是否可以序列化
                    if (Serializable.class.isAssignableFrom(outElementClass)
                            //基础类型
                            || outElementClass.isPrimitive()) {
                        return getSerializable();
                    }
                }
            } else if (CharSequence.class.isAssignableFrom(clazz)) {
                return new ParameterHandler<CharSequence>() {
                    @Override
                    public void apply(Bundle bundle, String key, @Nullable CharSequence value, boolean required) {
                        Utils.checkRequiredValue(key, value, required);
                        bundle.putCharSequence(key, value);
                    }
                };
            } else if (Parcelable.class.isAssignableFrom(clazz)) {
                return new ParameterHandler<Parcelable>() {
                    @Override
                    public void apply(Bundle bundle, String key, @Nullable Parcelable value, boolean required) {
                        Utils.checkRequiredValue(key, value, required);
                        bundle.putParcelable(key, value);
                    }
                };
            } else if (Serializable.class.isAssignableFrom(clazz)) {
                return getSerializable();
            }

        } else if (type instanceof ParameterizedType) {
            Class<?> rawType = getRawType(type);
            Type elementType = getParameterUpperBound(0, (ParameterizedType) type);
            Class<?> elementClass = getRawType(elementType);
            if (rawType == ArrayList.class) {
                if (Parcelable.class.isAssignableFrom(elementClass)) {
                    return new ParameterHandler<ArrayList<? extends Parcelable>>() {
                        @Override
                        public void apply(Bundle bundle, String key, @Nullable ArrayList<? extends Parcelable> value, boolean required) {
                            Utils.checkRequiredValue(key, value, required);
                            bundle.putParcelableArrayList(key, value);
                        }
                    };
                } else if (CharSequence.class.isAssignableFrom(elementClass)) {
                    return new ParameterHandler<ArrayList<CharSequence>>() {
                        @Override
                        public void apply(Bundle bundle, String key, @Nullable ArrayList<CharSequence> value, boolean required) {
                            Utils.checkRequiredValue(key, value, required);
                            bundle.putCharSequenceArrayList(key, value);
                        }
                    };
                }
            } else if (rawType == SparseArray.class) {
                if (Parcelable.class.isAssignableFrom(elementClass)) {
                    return new ParameterHandler<SparseArray<? extends Parcelable>>() {
                        @Override
                        public void apply(Bundle bundle, String key, @Nullable SparseArray<? extends Parcelable> value, boolean required) {
                            Utils.checkRequiredValue(key, value, required);
                            bundle.putSparseParcelableArray(key, value);
                        }
                    };
                }
            }
        }
        //GenericArrayType other ParameterizedType
        Class<?> rawType = getRawType(type);
        if (rawType.isArray()) {
            Class<?> outElementClass = getOutComponentType(rawType);
            if (Serializable.class.isAssignableFrom(outElementClass)) {
                return getSerializable();
            }
        }
        if (Serializable.class.isAssignableFrom(rawType)) {
            return getSerializable();
        }
        return null;
    }

    private static ParameterHandler<Serializable> getSerializable() {
        return new ParameterHandler<Serializable>() {
            @Override
            public void apply(Bundle bundle, String key, @Nullable Serializable value, boolean required) {
                Utils.checkRequiredValue(key, value, required);
                bundle.putSerializable(key, value);
            }
        };
    }

    /**
     * 获取最外层数组类型 如 String[][] 获取String
     */
    private static Class<?> getOutComponentType(Class<?> clazz) {
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
            Utils.checkNotNull(clazz, "clazz==null");
            return getOutComponentType(clazz);
        }
        return clazz;
    }
}