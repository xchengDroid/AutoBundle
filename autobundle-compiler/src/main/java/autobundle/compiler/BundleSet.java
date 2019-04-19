package autobundle.compiler;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
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

import static com.google.auto.common.MoreElements.getPackage;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * 创建时间：2019/4/4
 * 编写人： chengxin
 * 功能描述：
 */
class BundleSet {
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

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                //.addAnnotation(UI_THREAD)
                //Unchecked cast
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build())
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

            if (bundleBinding.type == TypeName.INT) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getInt");
            } else if (bundleBinding.type == TypeName.LONG) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getLong");
            } else if (bundleBinding.type == TypeName.BYTE) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getByte");
            } else if (bundleBinding.type == TypeName.SHORT) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getShort");
            } else if (bundleBinding.type == TypeName.DOUBLE) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getDouble");
            } else if (bundleBinding.type == TypeName.BOOLEAN) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getBoolean");
            } else if (bundleBinding.type == TypeName.FLOAT) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getFloat");
            } else if (bundleBinding.type == TypeName.CHAR) {
                addPrimitiveStatement(methodBuilder, bundleBinding, "getChar");
            } else {
                methodBuilder.addStatement("target.$L =($T) bundle.get($S)", bundleBinding.name, bundleBinding.type, bundleBinding.key);
                if (bundleBinding.required) {
                    methodBuilder.beginControlFlow("if (target." + bundleBinding.name + " == null)");
                    methodBuilder.addStatement(
                            "throw new $T(\"The required field '$L'"
                                    + " with key '$L'"
                                    + " is null, in class '\" + $T.class.getName() + \"' ."
                                    + " If this field is optional remove '@Required' annotation.\") ",
                            NULLPOINTEREXCEPTION, bundleBinding.name, bundleBinding.key, targetTypeName);
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
        //对应某个类中注解的value不能重复
        private final Map<String, FieldBundleBinding> valueFieldMap = new LinkedHashMap<>();
        //对应某个类中field不能有多个注解
        private final Map<String, FieldBundleBinding> nameFieldMap = new LinkedHashMap<>();

        private Builder(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal) {
            this.targetTypeName = targetTypeName;
            this.bindingClassName = bindingClassName;
            this.isFinal = isFinal;
        }

        void addField(FieldBundleBinding binding) {
            valueFieldMap.put(binding.key, binding);
            nameFieldMap.put(binding.name, binding);
        }

        void setParent(BundleSet parent) {
            this.parentBinding = parent;
        }

        @Nullable
        FieldBundleBinding findExistingBindingByValue(String value) {
            return valueFieldMap.get(value);
        }

        @Nullable
        FieldBundleBinding findExistingBindingByName(String name) {
            return nameFieldMap.get(name);
        }

        BundleSet build() {
            ImmutableList.Builder<FieldBundleBinding> viewBindings = ImmutableList.builder();
            for (FieldBundleBinding builder : valueFieldMap.values()) {
                viewBindings.add(builder);
            }
            return new BundleSet(targetTypeName, bindingClassName, isFinal, viewBindings.build(), parentBinding);
        }
    }
}
