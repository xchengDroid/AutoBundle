package autobundle.compiler;

import java.io.Serializable;
import java.util.Map;

//不包括在TypeKind里面的
enum OtherKind {
    MAP(Map.class.getCanonicalName()),
    ITERABLE(Iterable.class.getCanonicalName()),
    PARCELABLE("android.os.Parcelable"),
    SERIALIZABLE(Serializable.class.getCanonicalName());
    final String className;

    OtherKind(String className) {
        this.className = className;
    }
}
