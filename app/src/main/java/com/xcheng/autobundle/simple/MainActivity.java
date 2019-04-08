package com.xcheng.autobundle.simple;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class MainActivity extends TopActivity {

    @IntValue("_int")
    int _int;
    @BooleanValue("_bool")
    boolean _bool;
    @ShortValue("_short")
    short _short;
    @ByteValue("_byte")
    byte _byte;
    @CharValue("_char")
    char _char;
    @LongValue("_long")
    long _long;
    @DoubleValue("_double")
    double _double;
    @FloatValue("_float")
    float _float;

    @StringValue("_string")
    String _string;

    @CharSequenceValue("_CharSequence")
    CharSequence _CharSequence;

    @IntArrayValue("_IntArrayValue")
    int[] _IntArrayValue;
    @Nullable
    @BooleanArrayValue("_BooleanArrayValue")
    boolean[] _BooleanArrayValue;
    @CharArrayValue("_CharArrayValue")
    char[] _CharArrayValue;

    @CharSequenceArrayValue("_CharSequenceArrayValue")
    CharSequence[] _CharSequenceArrayValue;

    @DoubleArrayValue("_DoubleArrayValue")
    double[] _DoubleArrayValue;
    @FloatArrayValue("_FloatArrayValue")
    float[] _FloatArrayValue;

    @ShortArrayValue("_ShortArrayValue")
    short[] _ShortArrayValue;

    @ByteArrayValue("_ByteArrayValue")
    byte[] _ByteArrayValue;

    @LongArrayValue("_LongArrayValue")
    long[] _LongArrayValue;
    @ParcelableArrayValue("_ParcelableArrayValue")
    Parcelable[] _ParcelableArrayValue;


    @SparseParcelableArrayValue("_SparseParcelableArrayValue")
    SparseArray<Bundle> _SparseParcelableArrayValue;

    @StringArrayValue("_StringArrayValue")
    String[] _StringArrayValue;

    @CharSequenceArrayListValue("_CharSequenceArrayListValue")
    List<CharSequence> _CharSequenceArrayListValue;

    @IntegerArrayListValue("_IntegerArrayListValue")
    List<Integer> _IntegerArrayListValue;

    @ParcelableArrayListValue("_ParcelableArrayListValue")
    List<Parcelable> _ParcelableArrayListValue;

    @StringArrayListValue("_StringArrayListValue")
    ArrayList<String> _StringArrayListValue;

    @ParcelableValue("_ParcelableValue")
    Bundle _ParcelableValue;

    @SerializableValue("_SerializableValue")
    HashMap<String,Short> _SerializableValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public static void main(String[] args) {
    }

}
