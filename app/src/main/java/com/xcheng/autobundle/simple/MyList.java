package com.xcheng.autobundle.simple;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

/**
 * 创建时间：2019/4/10
 * 编写人： chengxin
 * 功能描述：
 */
public class MyList<E> extends SparseArray<E> implements Parcelable {

    public MyList(){


    }

    protected MyList(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyList> CREATOR = new Creator<MyList>() {
        @Override
        public MyList createFromParcel(Parcel in) {
            return new MyList(in);
        }

        @Override
        public MyList[] newArray(int size) {
            return new MyList[size];
        }
    };
}
