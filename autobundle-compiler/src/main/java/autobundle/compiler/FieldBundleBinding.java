package autobundle.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

final class FieldBundleBinding {
    //属性名称
    private final String name;
    //属性类型
    private final TypeName type;
    private final boolean required;
    //对应的key值
    private final String key;

    FieldBundleBinding(String name, String key, TypeName type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.key = key;
    }


    public String getName() {
        return name;
    }

    public TypeName getType() {
        return type;
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

    public boolean isRequired() {
        return required;
    }
}
