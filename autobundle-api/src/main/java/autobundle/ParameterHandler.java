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
