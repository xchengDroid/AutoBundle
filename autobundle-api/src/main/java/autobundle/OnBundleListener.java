package autobundle;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * 创建时间：2019/4/11
 * 编写人： chengxin
 * 功能描述：
 */
public interface OnBundleListener {

    void onBundling();

    void onCompleted(@NonNull Bundle bundle);
}
