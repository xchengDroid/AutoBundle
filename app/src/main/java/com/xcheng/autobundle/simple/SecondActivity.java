package com.xcheng.autobundle.simple;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.List;

import autobundle.AutoBundle;
import autobundle.annotation.Required;
import autobundle.annotation.Unbox;

public class SecondActivity extends TopActivity {
    @Required
    @Unbox("loginName")
    String loginName;
    @Required
    @Unbox("password")
    String password;

    @Unbox("int")
    int intValue;

    @Unbox("string")
    String stringValue;

    @Unbox("intArray")
    int[] intArrayValue;

    @Unbox("stringArray")
    String[] stringArrayValue;

    @Unbox("parcelable")
    Parcelable ParcelableValue;

    @Unbox("parcelableArray")
    Parcelable[] parcelableArrayValue;

    @Unbox("sparseParcelableArray")
    SparseArray<? extends Parcelable> sparseParcelableArrayValue;

    @Unbox("stringArrayList")
    List<String> stringArrayListValue;

    @Unbox("parcelableArrayList")
    List<? extends Parcelable> parcelableArrayListValue;

    @Unbox("serializable")
    Serializable serializableValue;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        AutoBundle.getInstance().bind(this);
    }
}
