package autobundle.compiler;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;

/**
 * 创建时间：2019/4/4
 * 编写人： chengxin
 * 功能描述：
 */
public class BundleSet {
    private final TypeName targetTypeName;
    private final ClassName bindingClassName;
    private final ImmutableList<FieldBundleBinding> bundleBindings;
    private final @Nullable
    BundleSet parentBinding;
    private boolean isFinal;

    public BundleSet(TypeName targetTypeName,
                     ClassName bindingClassName,
                     boolean isFinal,
                     ImmutableList<FieldBundleBinding> bundleBindings,
                     @Nullable BundleSet parentBinding) {
        this.targetTypeName = targetTypeName;
        this.bindingClassName = bindingClassName;
        this.bundleBindings = bundleBindings;
        this.parentBinding = parentBinding;
        this.isFinal = isFinal;
    }

    static Builder newBuilder(TypeElement enclosingElement) {
        TypeMirror typeMirror = enclosingElement.asType();
        TypeName targetType = TypeName.get(typeMirror);
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }

        String packageName = getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        ClassName bindingClassName = ClassName.get(packageName, className + "_BundleBinding");

        boolean isFinal = enclosingElement.getModifiers().contains(Modifier.FINAL);
        return new Builder(targetType, bindingClassName, isFinal);
    }

    static final class Builder {
        private final TypeName targetTypeName;
        private final ClassName bindingClassName;
        private final boolean isFinal;

        private @Nullable
        BundleSet parentBinding;
        private final Map<String, FieldBundleBinding> viewIdMap = new LinkedHashMap<>();

        private Builder(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal) {
            this.targetTypeName = targetTypeName;
            this.bindingClassName = bindingClassName;
            this.isFinal = isFinal;
        }

        void addField(String name, FieldBundleBinding binding) {
            viewIdMap.put(name, binding);
        }

        void setParent(BundleSet parent) {
            this.parentBinding = parent;
        }

        @Nullable
        FieldBundleBinding findExistingBinding(String name) {
            return viewIdMap.get(name);
        }

        BundleSet build() {
            ImmutableList.Builder<FieldBundleBinding> viewBindings = ImmutableList.builder();
            for (FieldBundleBinding builder : viewIdMap.values()) {
                viewBindings.add(builder);
            }
            return new BundleSet(targetTypeName, bindingClassName, isFinal, viewBindings.build(), parentBinding);
        }
    }
}
