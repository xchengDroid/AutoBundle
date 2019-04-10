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

abstract class ParameterHandler<T> {
    final String key;
    //Primitive type wont be check!
    final boolean required;

    ParameterHandler(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    abstract void apply(Bundle bundle, @Nullable T value);

    static final class IntParameterHandler extends ParameterHandler<Integer> {

        IntParameterHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Integer value) {
            bundle.putInt(key, value);
        }
    }


    static final class BooleanParameterHandler extends ParameterHandler<Boolean> {
        BooleanParameterHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, Boolean value) {
            bundle.putBoolean(key, value);
        }
    }

    static final class StringParameterHandler extends ParameterHandler<String> {

        StringParameterHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable String value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putString(key, value);
        }
    }

    static final class CharSequenceParameterHandler extends ParameterHandler<CharSequence> {

        CharSequenceParameterHandler(String key, boolean required) {
            super(key, required);
        }

        @Override
        void apply(Bundle bundle, @Nullable CharSequence value) {
            Utils.checkRequiredValue(key, value, required);
            bundle.putCharSequence(key, value);
        }
    }
}
