package autobundle;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import autobundle.ParameterHandler.Factory;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * 创建时间：2019/4/1
 * 编写人： chengxin
 * 功能描述：to box and unbox bundle
 */
public final class AutoBundle {
    static final String TAG = "AutoBundle";
    private final Map<Class<?>, Constructor<? extends IBinder>> BINDINGS = new LinkedHashMap<>();
    private static volatile AutoBundle defaultInstance;

    final boolean validateEagerly;
    final boolean debug;
    final List<Factory> factories;
    final List<OnBundleListener> listeners;

    public static AutoBundle getDefault() {
        if (defaultInstance == null) {
            synchronized (AutoBundle.class) {
                if (defaultInstance == null) {
                    defaultInstance = new Builder().build();
                }
            }
        }
        return defaultInstance;
    }

    private AutoBundle(boolean validateEagerly, boolean debug,
                       List<Factory> factories, List<OnBundleListener> listeners) {
        this.validateEagerly = validateEagerly;
        this.debug = debug;
        this.listeners = listeners;
        this.factories = factories;
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
        private boolean validateEagerly;
        private boolean debug;
        private List<OnBundleListener> listeners;
        private List<Factory> factories;

        private Builder() {
        }

        /**
         * Control whether debug logging is enabled.
         */
        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * When calling {@link #create} on the resulting {@link AutoBundle} instance, eagerly validate
         * the configuration of all methods in the supplied interface.
         */
        public Builder validateEagerly(boolean validateEagerly) {
            this.validateEagerly = validateEagerly;
            return this;
        }

        public Builder addOnBundleListener(OnBundleListener listener) {
            Utils.checkNotNull(listener, "listener=null");
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            listeners.add(listener);
            return this;
        }

        /**
         * Add a parameterHandler factory for serialization and deserialization of objects.
         */
        public Builder addParameterHandlerFactory(Factory factory) {
            Utils.checkNotNull(factory, "factory == null");
            if (factories == null) {
                factories = new ArrayList<>();
            }
            factories.add(factory);
            return this;
        }

        /**
         * Installs the default AutoBundle returned by {@link AutoBundle#getDefault()} using this builders' values. Must be
         * done only once before the first usage of the default AutoBundle.
         *
         * @throws IllegalStateException if there's already a default AutoBundle instance in place
         */
        public AutoBundle installDefault() {
            //like EventBus ImageLoader LocalBroadcastManager android-job#JobManager Android-skin-support#SkinCompatManager
            synchronized (AutoBundle.class) {
                if (defaultInstance != null) {
                    throw new IllegalStateException("Default instance already exists." +
                            " It may be only set once before it's used the first time to ensure consistent behavior.");
                }
                defaultInstance = build();
                return defaultInstance;
            }
        }

        /**
         * Builds an AutoBundle based on the current configuration.
         */
        public AutoBundle build() {
            List<OnBundleListener> listeners = this.listeners;
            if (listeners == null) {
                listeners = emptyList();
            } else {
                listeners = unmodifiableList(new ArrayList<>(listeners));
            }

            // Make a defensive copy of the factories.
            List<Factory> factories = new ArrayList<>();
            factories.add(BuiltInHandlerFactory.INSTANCE);
            if (this.factories != null) {
                factories.addAll(this.factories);
            }
            factories.add(BestGuessHandlerFactory.INSTANCE);
            return new AutoBundle(validateEagerly, debug, unmodifiableList(factories), listeners);
        }
    }
}
