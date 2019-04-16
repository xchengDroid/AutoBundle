# AutoBundle


AutoBundle generates boilerplate code for field binding with ``android.os.Bundle``.

1. Usage
2. Download
3. Thanks
4. License

## Usage

1. Generate builder method
2. Bind annotated fields
3. Store annotated fields

### 1. Generate builder method

In your class which has state from `Bundle`
 (`Activity`, `BroadcastReceiver`, `Service`, `Fragment` or others),



 `@Box`

for bundle.putXXX  with parameters

 `@Unbox` 

 bundle.getXXXX  with fields

`@Required`

if field or parameter is composite type , add  this annotation will check not null

`@BundFlag`

if add  this annotation, callback of create Method will get a flag

#### 2.  create AutoBundle Instance

```java
 
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



#### 3.Here is example for Activity.

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

#### how to bind fields from bundle

```
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //auto bind the field from bundle
        AutoBundle.getInstance().bind(this);
    }
```



#### 4.And you can create Bundle  like retrofit

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



#### 5.Create Bundle method by  annotated parameter

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
- 

## Download

```groovy
dependencies {
    implementation 'com.xcheng:autobundle-api:1.2.1'
    annotationProcessor 'com.xcheng:autobundle-compiler:1.2.1'
}
```

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
