package autobundle.compiler;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import autobundle.annotation.BooleanArrayValue;
import autobundle.annotation.BooleanValue;
import autobundle.annotation.ByteArrayValue;
import autobundle.annotation.ByteValue;
import autobundle.annotation.CharArrayValue;
import autobundle.annotation.CharSequenceArrayListValue;
import autobundle.annotation.CharSequenceArrayValue;
import autobundle.annotation.CharSequenceValue;
import autobundle.annotation.CharValue;
import autobundle.annotation.DoubleArrayValue;
import autobundle.annotation.DoubleValue;
import autobundle.annotation.FloatArrayValue;
import autobundle.annotation.FloatValue;
import autobundle.annotation.IntArrayValue;
import autobundle.annotation.IntValue;
import autobundle.annotation.IntegerArrayListValue;
import autobundle.annotation.LongArrayValue;
import autobundle.annotation.LongValue;
import autobundle.annotation.ParcelableArrayListValue;
import autobundle.annotation.ParcelableArrayValue;
import autobundle.annotation.ParcelableValue;
import autobundle.annotation.SerializableValue;
import autobundle.annotation.ShortArrayValue;
import autobundle.annotation.ShortValue;
import autobundle.annotation.SparseParcelableArrayValue;
import autobundle.annotation.StringArrayListValue;
import autobundle.annotation.StringArrayValue;
import autobundle.annotation.StringValue;

import static com.google.auto.common.MoreElements.getPackage;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * 创建时间：2019/4/4
 * 编写人： chengxin
 * 功能描述：
 */
class BundleSet {
    private final String METHOD_BIND = "bind";
    private static final ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    private static final ClassName NULLPOINTEREXCEPTION = ClassName.get("java.lang", "NullPointerException");

    private static final ClassName UI_THREAD =
            ClassName.get("android.support.annotation", "UiThread");
    private static final ClassName CALL_SUPER =
            ClassName.get("android.support.annotation", "CallSuper");
    private static final ClassName SUPPRESS_LINT =
            ClassName.get("android.annotation", "SuppressLint");
    private static final ClassName IBINDER = ClassName.get("autobundle", "IBinder");

    private final TypeName targetTypeName;
    private final ClassName bindingClassName;
    private final ImmutableList<FieldBundleBinding> bundleBindings;
    private final @Nullable
    BundleSet parentBinding;
    private boolean isFinal;

    private BundleSet(TypeName targetTypeName,
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


    JavaFile brewJava() {
        TypeSpec bindingConfiguration = createType();
        return JavaFile.builder(bindingClassName.packageName(), bindingConfiguration)
                .addFileComment("Generated code from AutoBundle. Do not modify!")
                .build();
    }

    private TypeSpec createType() {
        TypeSpec.Builder result = TypeSpec.classBuilder(bindingClassName.simpleName())
                .addModifiers(PUBLIC);
        if (isFinal) {
            result.addModifiers(FINAL);
        }

        if (parentBinding != null) {
            result.superclass(parentBinding.bindingClassName);
        } else {
            result.addSuperinterface(IBINDER);
        }

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(METHOD_BIND)
                .addAnnotation(UI_THREAD)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.OBJECT, "object")
                .addParameter(BUNDLE, "bundle");

        if (!isFinal && parentBinding == null) {
            methodBuilder.addAnnotation(CALL_SUPER);
        }
        if (parentBinding != null) {
            methodBuilder.addStatement("super.bind(object, bundle)");
        }
        methodBuilder.addStatement("$T target = ($T)object", targetTypeName, targetTypeName);
        methodBuilder.addCode("\n");

        for (FieldBundleBinding bundleBinding : bundleBindings) {

            if (bundleBinding.annotationClass == IntValue.class) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getInt");
            } else if (bundleBinding.annotationClass == LongValue.class) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getLong");
            } else if (bundleBinding.annotationClass == ByteValue.class) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getByte");
            } else if (bundleBinding.annotationClass == ShortValue.class) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getShort");
            } else if (bundleBinding.annotationClass == DoubleValue.class) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getDouble");
            } else if (bundleBinding.annotationClass == BooleanValue.class) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getBoolean");
            } else if (bundleBinding.annotationClass == FloatValue.class) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getFloat");
            } else if (bundleBinding.annotationClass == CharValue.class) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getChar");
            } else if (bundleBinding.annotationClass == StringValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getString");
            } else if (bundleBinding.annotationClass == CharSequenceValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getCharSequence");
            } else if (bundleBinding.annotationClass == IntArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getIntArray");
            } else if (bundleBinding.annotationClass == BooleanArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getBooleanArray");
            } else if (bundleBinding.annotationClass == CharArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getCharArray");
            } else if (bundleBinding.annotationClass == CharSequenceArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getCharSequenceArray");
            } else if (bundleBinding.annotationClass == DoubleArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getDoubleArray");
            } else if (bundleBinding.annotationClass == FloatArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getFloatArray");
            } else if (bundleBinding.annotationClass == ShortArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getShortArray");
            } else if (bundleBinding.annotationClass == ByteArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getByteArray");
            } else if (bundleBinding.annotationClass == LongArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getLongArray");
            } else if (bundleBinding.annotationClass == ParcelableArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getParcelableArray");
            } else if (bundleBinding.annotationClass == SparseParcelableArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getSparseParcelableArray");
            } else if (bundleBinding.annotationClass == StringArrayValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getStringArray");
            } else if (bundleBinding.annotationClass == CharSequenceArrayListValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getCharSequenceArrayList");
            } else if (bundleBinding.annotationClass == IntegerArrayListValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getIntegerArrayList");
            } else if (bundleBinding.annotationClass == ParcelableArrayListValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getParcelableArrayList");
            } else if (bundleBinding.annotationClass == StringArrayListValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getStringArrayList");
            } else if (bundleBinding.annotationClass == ParcelableValue.class) {
                addCompositeStatement(methodBuilder, bundleBinding, "getParcelable");
            } else if (bundleBinding.annotationClass == SerializableValue.class) {
                methodBuilder.addStatement("target.$L =($T) bundle.getSerializable($S)", bundleBinding.name, bundleBinding.type, bundleBinding.key);
                if (bundleBinding.required) {
                    methodBuilder.beginControlFlow("if (target." + bundleBinding.name + " == null)");
                    methodBuilder.addStatement(
                            "throw new $T(\"The field '" + bundleBinding.name + "' is null, in class '\" + $T.class.getName() + \"!\")", NULLPOINTEREXCEPTION, targetTypeName);
                    methodBuilder.endControlFlow();
                }
            }
        }
        result.addMethod(methodBuilder.build());
        return result.build();
    }


    private void addPrimitiveStatement(MethodSpec.Builder methodBuilder, FieldBundleBinding bundleBinding, String getMethodName) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("target.$L = ", bundleBinding.name)
                .add("bundle.");
        builder.add(getMethodName + "($S," + getDefaultValue(bundleBinding) + ")", bundleBinding.key);
        //这样子会换行 自动加 结束符;哈哈
        methodBuilder.addStatement("$L", builder.build());
    }

    private void addCompositeStatement(MethodSpec.Builder methodBuilder, FieldBundleBinding bundleBinding, String getMethodName) {
        methodBuilder.addStatement("target.$L = bundle.$L($S)", bundleBinding.name, getMethodName, bundleBinding.key);
        if (bundleBinding.required) {
            methodBuilder.beginControlFlow("if (target." + bundleBinding.name + " == null)");
            methodBuilder.addStatement(
                    "throw new $T(\"The field '" + bundleBinding.name + "' is null, in class '\" + $T.class.getName() + \"!\")", NULLPOINTEREXCEPTION, targetTypeName);
            methodBuilder.endControlFlow();
        }
    }

    private String getDefaultValue(FieldBundleBinding bundleBinding) {
        return "target." + bundleBinding.name;
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
