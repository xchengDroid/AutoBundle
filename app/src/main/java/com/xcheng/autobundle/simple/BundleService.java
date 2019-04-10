package com.xcheng.autobundle.simple;

import android.os.Bundle;

import autobundle.annotation.SparseParcelableArrayValue;
import autobundle.annotation.StringValue;

/**
 * 创建时间：2019/4/10
 * 编写人： chengxin
 * 功能描述：
 */
public interface BundleService {

    Bundle loginBundle(@StringValue("name") String name);
    Bundle charSequenceArray(@SparseParcelableArrayValue("charSequenceArrayList") MyList<Bundle> age);

}
