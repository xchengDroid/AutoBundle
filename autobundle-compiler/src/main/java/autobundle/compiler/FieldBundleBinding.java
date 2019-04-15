package autobundle.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

final class FieldBundleBinding {
    //属性名称
    final String name;
    //属性类型
    final TypeName type;
    //是否必须  对复合数据类型有效
    final boolean required;
    //对应的key值
    final String key;

    FieldBundleBinding(String name, String key, TypeName type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.key = key;
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
