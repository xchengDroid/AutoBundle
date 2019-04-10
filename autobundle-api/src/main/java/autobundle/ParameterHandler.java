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

abstract class ParameterHandler<T> {
    final String key;
    //Primitive type wont be check!
    final boolean required;

    ParameterHandler(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    abstract void apply(Bundle bundle, @Nullable T value);

    static final class IntHandler extends ParameterHandler<Integer> {

        IntHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Integer value) {
            bundle.putInt(key, value);
        }
    }


    static final class LongHandler extends ParameterHandler<Long> {

        LongHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Long value) {
            bundle.putLong(key, value);
        }
    }

    static final class DoubleHandler extends ParameterHandler<Double> {

        DoubleHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Double value) {
            bundle.putDouble(key, value);
        }
    }

    static final class FloatHandler extends ParameterHandler<Float> {

        FloatHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Float value) {
            bundle.putFloat(key, value);
        }
    }

    static final class ByteHandler extends ParameterHandler<Byte> {

        ByteHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Byte value) {
            bundle.putByte(key, value);
        }
    }

    static final class ShortHandler extends ParameterHandler<Short> {

        ShortHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Short value) {
            bundle.putShort(key, value);
        }
    }

    static final class CharacterHandler extends ParameterHandler<Character> {

        CharacterHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Character value) {
            bundle.putChar(key, value);
        }
    }

    static final class BooleanHandler extends ParameterHandler<Boolean> {
        BooleanHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Boolean value) {
            bundle.putBoolean(key, value);
        }
    }

    static final class BooleanArrayHandler extends ParameterHandler<boolean[]> {
        BooleanArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable boolean[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putBooleanArray(key, value);

        }
    }

    static final class ByteArrayHandler extends ParameterHandler<byte[]> {
        ByteArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable byte[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putByteArray(key, value);
        }
    }

    static final class CharArrayHandler extends ParameterHandler<char[]> {
        CharArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable char[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putCharArray(key, value);
        }
    }

    static final class DoubleArrayHandler extends ParameterHandler<double[]> {
        DoubleArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable double[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putDoubleArray(key, value);
        }
    }

    static final class FloatArrayHandler extends ParameterHandler<float[]> {
        FloatArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable float[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putFloatArray(key, value);
        }
    }

    static final class IntArrayHandler extends ParameterHandler<int[]> {
        IntArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable int[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putIntArray(key, value);
        }
    }

    static final class LongArrayHandler extends ParameterHandler<long[]> {
        LongArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable long[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putLongArray(key, value);
        }
    }

    static final class ShortArrayHandler extends ParameterHandler<short[]> {
        ShortArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable short[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putShortArray(key, value);
        }
    }

    static final class ParcelableArrayHandler extends ParameterHandler<Parcelable[]> {
        ParcelableArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable Parcelable[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putParcelableArray(key, value);
        }
    }

    static final class CharSequenceArrayHandler extends ParameterHandler<CharSequence[]> {
        CharSequenceArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable CharSequence[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putCharSequenceArray(key, value);
        }
    }

    static final class StringArrayHandler extends ParameterHandler<String[]> {
        StringArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable String[] value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putStringArray(key, value);
        }
    }

    static final class SparseArrayHandler extends ParameterHandler<SparseArray<? extends Parcelable>> {
        SparseArrayHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable SparseArray<? extends Parcelable> value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putSparseParcelableArray(key, value);
        }
    }


    static final class StringHandler extends ParameterHandler<String> {

        StringHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable String value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putString(key, value);
        }
    }

    static final class CharSequenceHandler extends ParameterHandler<CharSequence> {

        CharSequenceHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable CharSequence value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putCharSequence(key, value);
        }
    }
}
