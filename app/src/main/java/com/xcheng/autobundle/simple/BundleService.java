package com.xcheng.autobundle.simple;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

import autobundle.annotation.BundleFlag;
import autobundle.annotation.IntArrayValue;
import autobundle.annotation.IntValue;
import autobundle.annotation.ParcelableArrayListValue;
import autobundle.annotation.ParcelableArrayValue;
import autobundle.annotation.ParcelableValue;
import autobundle.annotation.Required;
import autobundle.annotation.SerializableValue;
import autobundle.annotation.SparseParcelableArrayValue;
import autobundle.annotation.StringArrayListValue;
import autobundle.annotation.StringArrayValue;
import autobundle.annotation.StringValue;

/**
 * 创建时间：2019/4/10
 * 编写人： chengxin
 * 功能描述：
 */
public interface BundleService {
    @BundleFlag(1)
    Bundle intBundle(@IntValue("_int") int value);

    @BundleFlag(2)
    Bundle stringBundle(@StringValue("_String") String value);

    @BundleFlag(3)
    Bundle intArrayBundle(@IntArrayValue("_intArray") int[] value);

    Bundle stringArrayBundle(@StringArrayValue("_StringArray") String[] value);

    Bundle parcelableArrayBundle(@ParcelableArrayValue("_ParcelableArray") Parcelable[] value);

    Bundle sparseParcelableArrayBundle(@Required @SparseParcelableArrayValue("_SparseParcelableArray")
                                               SparseArray<? extends Parcelable> value);

    Bundle stringArrayListBundle(@Required @StringArrayListValue("_StringArrayListValue") ArrayList<String> value);

    Bundle parcelableArrayListBundle(@ParcelableArrayListValue("_ParcelableArrayList") ArrayList<? extends Parcelable> value);

    Bundle parcelableBundle(@ParcelableValue("_ParcelableArrayList") Parcelable value);

    Bundle parcelableBundle(@SerializableValue("_SerializableValue") Serializable value);

}
