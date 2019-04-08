package autobundle;

import android.os.Bundle;

import java.lang.reflect.Method;

/**
 * 创建时间：2019/4/1
 * 编写人： chengxin
 * 功能描述：
 */
public class BundleMethod {
    private final Method method;

    public BundleMethod(Method method) {
        this.method = method;
    }

    Bundle invoke(Object[] args) {
        return null;
    }
}
