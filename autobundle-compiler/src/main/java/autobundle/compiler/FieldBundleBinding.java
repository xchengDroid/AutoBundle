package autobundle.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

final class FieldBundleBinding {
    //属性名称
    final String name;
    //属性类型
    final TypeName type;
    final boolean required;
    //对应的key值
    final String key;
    final Class<? extends Annotation> annotationClass;

    FieldBundleBinding(String name, Class<? extends Annotation> annotationClass, String key, TypeName type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.key = key;
        this.annotationClass = annotationClass;
    }

    public ClassName getRawType() {
        if (type instanceof ParameterizedTypeName) {
            return ((ParameterizedTypeName) type).rawType;
        }
        return (ClassName) type;
    }

    public String getDescription() {
        return "field '" + name + "'";
    }


}
