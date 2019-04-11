package com.xcheng.autobundle.simple;

import android.os.Bundle;

import autobundle.annotation.BundleFlag;
import autobundle.annotation.CharSequenceArrayValue;
import autobundle.annotation.SparseParcelableArrayValue;
import autobundle.annotation.StringValue;

/**
 * 创建时间：2019/4/10
 * 编写人： chengxin
 * 功能描述：
 */
public interface BundleService {
    @BundleFlag(1)
    Bundle loginBundle(@StringValue("name") String name);
    @BundleFlag(100)
    Bundle charSequenceArray(@CharSequenceArrayValue("charSequenceArrayList") String[] charSequences);

    Bundle charSequenceArrayList(@SparseParcelableArrayValue("charSequenceArrayList") MyList<Bundle> age);

}
