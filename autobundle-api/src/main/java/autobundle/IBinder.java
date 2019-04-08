package autobundle;

import android.os.Bundle;
import android.support.annotation.UiThread;

/**
 * An binder contract that will unbind views when called.
 */
public interface IBinder {
    @UiThread
    void bind(Object object, Bundle bundle);
}