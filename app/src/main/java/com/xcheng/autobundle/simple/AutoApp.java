package com.xcheng.autobundle.simple;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import autobundle.AutoBundle;
import autobundle.OnBundleListener;

/**
 * 创建时间：2019/4/11
 * 编写人： chengxin
 * 功能描述：
 */
public class AutoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AutoBundle.builder().debug(true)
                .validateEagerly(true)
                .addListener(new OnBundleListener() {
                    @Override
                    public void onBundling(int flag, String key, @Nullable Object value, boolean required) {
                        Log.e("print", "key");
                    }

                    @Override
                    public void onCompleted(int flag, @NonNull Bundle bundle) {
                        Log.e("print", bundle.toString());
                    }
                })
                .install();

    }
}
