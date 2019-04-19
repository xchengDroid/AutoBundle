package autobundle;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 创建时间：2019/4/11
 * 编写人： chengxin
 * 功能描述：
 */
public interface OnBundleListener {

    void onBundling(int flag, String key, @Nullable Object value, boolean required);

    /**
     * you can replace or add any key value in bundle
     */
    void onCompleted(int flag, Bundle bundle);
}
