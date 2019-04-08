package autobundle;

import android.support.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建时间：2019/4/1
 * 编写人： chengxin
 * 功能描述：
 */
public class AutoBundle {

    private static final Map<Method, BundleMethod> bundleMethodCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    private final Object[] emptyArgs = new Object[0];

                    @Override
                    public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        return loadServiceMethod(method).invoke(args != null ? args : emptyArgs);
                    }
                });
    }

    static BundleMethod loadServiceMethod(Method method) {
        BundleMethod result = bundleMethodCache.get(method);
        if (result != null) return result;

        synchronized (bundleMethodCache) {
            result = bundleMethodCache.get(method);
            if (result == null) {
                result = parseAnnotations(method);
                bundleMethodCache.put(method, result);
            }
        }
        return result;
    }

    static BundleMethod parseAnnotations(Method method) {

        return null;
    }

}
