package autobundle;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建时间：2019/4/1
 * 编写人： chengxin
 * 功能描述：
 */

public class AutoBundle {

    private static final String TAG = "AutoBundle";
    private static volatile AutoBundle defaultInstance;
    @VisibleForTesting
    final Map<Class<?>, Constructor<? extends IBinder>> BINDINGS = new LinkedHashMap<>();
    final boolean validateEagerly;
    final boolean debug;
    @NonNull
    final List<OnBundledListener> listeners;

    public static AutoBundle getDefault() {
        if (defaultInstance == null) {
            synchronized (AutoBundle.class) {
                if (defaultInstance == null) {
                    defaultInstance = new AutoBundle();
                }
            }
        }
        return defaultInstance;
    }

    private AutoBundle() {
        this(new Builder());
    }

    private AutoBundle(Builder builder) {
        this.validateEagerly = builder.validateEagerly;
        this.debug = builder.debug;
        this.listeners = builder.listeners;
    }

    public static Builder builder() {
        return new Builder();
    }


    @UiThread
    public void bind(@NonNull Activity target) {
        Bundle bundle = target.getIntent().getExtras();
        if (bundle == null) {
            throw new NullPointerException("bundle==null");
        }
        bind(target, bundle);
    }

    @UiThread
    public void bind(@NonNull Object target, @NonNull Bundle bundle) {
        Class<?> targetClass = target.getClass();
        if (debug) Log.d(TAG, "Looking up binding for " + targetClass.getName());

        Constructor<? extends IBinder> constructor = findBindingConstructorForClass(targetClass);
        if (constructor == null) {
            return;
        }

        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            IBinder iBinder = constructor.newInstance();
            iBinder.bind(target, bundle);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    @Nullable
    @CheckResult
    @UiThread
    private Constructor<? extends IBinder> findBindingConstructorForClass(Class<?> cls) {
        Constructor<? extends IBinder> bindingCtor = BINDINGS.get(cls);
        if (bindingCtor != null || BINDINGS.containsKey(cls)) {
            if (debug) Log.d(TAG, "HIT: Cached in binding map.");
            return bindingCtor;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")
                || clsName.startsWith("androidx.")) {
            if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return null;
        }
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_BundleBinding");
            //noinspection unchecked
            bindingCtor = (Constructor<? extends IBinder>) bindingClass.getConstructor();
            if (debug) Log.d(TAG, "HIT: Loaded binding class and constructor.");
        } catch (ClassNotFoundException e) {
            if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingCtor);
        return bindingCtor;
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <T> T create(final Class<T> service) {
        Utils.validateServiceInterface(service);
        if (validateEagerly) {
            eagerlyValidateMethods(service);
        }
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    private final Object[] emptyArgs = new Object[0];

                    @Override
                    public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        if (Utils.isDefaultMethod(method)) {
                            throw new UnsupportedOperationException();
                        }
                        return BundleFactory.loadBundleFactory(method).invoke(args != null ? args : emptyArgs);
                    }
                });
    }

    private void eagerlyValidateMethods(Class<?> service) {
        for (Method method : service.getDeclaredMethods()) {
            if (!Utils.isDefaultMethod(method)) {
                BundleFactory.loadBundleFactory(method);
            }
        }
    }

    public static final class Builder {
        boolean validateEagerly;
        boolean debug;
        List<OnBundledListener> listeners;

        private Builder() {
            //noinspection unchecked
            listeners = Collections.EMPTY_LIST;
            validateEagerly = false;
            debug = false;
        }

        public Builder debug() {
            debug = true;
            return this;
        }

        public Builder validateEagerly() {
            validateEagerly = true;
            return this;
        }

        public Builder addListener(OnBundledListener listener) {
            if (listeners == Collections.EMPTY_LIST) {
                listeners = new ArrayList<>();
            }
            listeners.add(listener);
            return this;
        }

        public AutoBundle installDefault() {
            synchronized (AutoBundle.class) {
                if (defaultInstance != null) {
                    throw new IllegalStateException("Default instance already exists." +
                            " It may be only set once before it's used the first time to ensure consistent behavior.");
                }
                defaultInstance = new AutoBundle(this);
                return defaultInstance;
            }
        }
    }
}
