package autobundle;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建时间：2019/4/1
 * 编写人： chengxin
 * 功能描述：
 */

public class AutoBundle {

    private static final Map<Method, BundleMethod> bundleMethodCache = new ConcurrentHashMap<>();


//    @NonNull
//    @UiThread
//    public static IBinder bind(Activity target) {
//        View sourceView = target.getWindow().getDecorView();
//        return bind(target, target.getIntent().getExtras());
//    }

//    @NonNull
//    @UiThread
//    public static IBinder bind(@NonNull Object target, Bundle bundle) {
//        View sourceView = source.getWindow().getDecorView();
//        return bind(target, sourceView);
//    }

}
