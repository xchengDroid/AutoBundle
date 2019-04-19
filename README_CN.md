### 前言

> android.os.Bundle 相信每一个Android 开发者都在使用，构建bundle和获取其中value时如果参数很多写起来代码很呆板，多次调用的话 key值容易写错，不易管理，AutoBundle的产生就是为了解决这些问题。AutoBundle采用动态代理的方式创建Bundle,采用编译时注解的方式为属性赋值。

##### 

## 使用

1. 成员属性 和  方法参数 注解解析
2. 创建单例
3. Activity中如何使用
4. 定义接口方法
5. 动态代理创建Bundle

### 1. 注解

In your class which has state from `Bundle`
 (`Activity`, `BroadcastReceiver`, `Service`, `Fragment` or others),

 `@Box`

在接口方法的参数上添加此注解 自动方法的值put进bundle

 `@Unbox` 

 在Activity、Fragment 或者任意类中的成员属性添加此注解，自动获取其值

`@Required`

如果添加此注解，当接口方法传入或从Bundle中获取的值为`null`时会crash

`@BundleFlag`

用于标记创建Bundle的接口方法，可以在回调函数中通过判断此注解的value做二次修改替换等

#### 2.  创建单例

```
 
 AutoBundle.builder().debug(true)//allow print debug message
                //check all create method before use
                .validateEagerly(true)
                .addListener(new OnBundleListener() {
                    /**
                     * @param flag  flag is @BundFlag value
                     */
                    @Override
                    public void onBundling(int flag, String key, @Nullable Object value, boolean required) {
                        Log.e("print", "key:" + key + " flag:" + flag);
                    }

                    @Override
                    public void onCompleted(int flag, @NonNull Bundle bundle) {
                        Log.e("print", bundle.toString());
                        //replace or add
                        // for example
                        if (flag = xxx) {
                            bundle.put("xxx", xxx);
                        }
                    }
                })
                .install();

```



#### 3.Activity 中使用

```java
public class MyActivity extends Activity {
    // field must not be private/protected.
    @Required
    @Unbox("loginName")
    String loginName;
    @Required
    @Unbox("password")
    String password;

    @Unbox("int")
    int intValue;

    @Unbox("string")
    String stringValue;

    @Unbox("intArray")
    int[] intArrayValue;

    @Unbox("stringArray")
    String[] stringArrayValue;

    @Unbox("parcelable")
    Parcelable ParcelableValue;

    @Unbox("parcelableArray")
    Parcelable[] parcelableArrayValue;

    @Unbox("sparseParcelableArray")
    SparseArray<? extends Parcelable> sparseParcelableArrayValue;

    @Unbox("stringArrayList")
    List<String> stringArrayListValue;

    @Unbox("parcelableArrayList")
    List<? extends Parcelable> parcelableArrayListValue;

    @Unbox("serializable")
    Serializable serializableValue;
}
```

##### 如何给属性自动赋值

```
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //auto bind the field from bundle
        AutoBundle.getInstance().bind(this);
    }
```



#### 4.像Retrofit创建Call一样创建Bundle

```java
public interface BundleService {
    @BundleFlag(0)
    Bundle getLogin(@Required @Box("loginName") String loginName,
                    @Required @Box("password") String password);

    @BundleFlag(1)
    Bundle getInt(@Box("int") int value);

    @BundleFlag(2)
    Bundle getString(@Box("string") String value);

    @BundleFlag(3)
    Bundle getIntArray(@Box("intArray") int[][] value);

    Bundle getStringArray(@Box("stringArray") String[] value);

    Bundle getParcelable(@Box("parcelable") Parcelable value);

    Bundle getParcelableArray(@Box("parcelableArray") Parcelable[] value);

    Bundle getSparseParcelableArray(@Box("sparseParcelableArray")
                                            SparseArray<? extends Parcelable> value);

    Bundle getStringArrayList(@Box("stringArrayList") ArrayList<String> value);

    Bundle getParcelableArrayList(@Box("parcelableArrayList") ArrayList<? extends Parcelable> value);

    Bundle getSerializable(@Box("serializable") Serializable value);
}
```



#### 5.接口方法调用创建Bundle

```java
//second step: you can crate Bundle like retrofit
   Bundle loginBundle = AutoBundle.getInstance()
                .create(BundleService.class)
                .getLogin("JackWharton","123456");

  Bundle intBundle = AutoBundle.getInstance()
                .create(BundleService.class)
                .getInt(1228);



```



In target class, Call binding method in ``onCreate``.

- ``bind(Object target, Bundle bundle)``
- ``bind(Activity target)`` (equals to ``bind(activity, activity.getIntent().getExtras())``)

## Download

```groovy
dependencies {
    implementation 'com.xcheng:autobundle-api:1.1.0'
    annotationProcessor 'com.xcheng:autobundle-compiler:1.1.0'
}
```

GitHub 地址：[ AutoBundle](<https://github.com/xchengDroid/AutoBundle>)



## Thanks

- [Retrofit](https://github.com/square/retrofit)
- [ButterKnife](https://github.com/JakeWharton/butterknife)

## License

```
Copyright 2019 xchengDroid

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```