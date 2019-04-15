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

declare fields with

 `@IntValue`.  

 bundle.putInt() or bundle.getInt()

 `@BooleanValue` 

 bundle.putBoolean()  or bundle.getBoolean()

`@StringVaulue` 

 bundle.putString()  or bundle.getString()

…….... have many other annotations for arrays and lists

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
    @IntValue("_int")
    int _int;
    
    @BooleanValue("_bool")
    boolean _bool;
    
    @ShortValue("_short")
    short _short;
    
    @ByteValue("_byte")
    byte _byte;
    
    @CharValue("_char")
    char _char;

    @LongValue("_long")
    long _long;
    
    @DoubleValue("_double")
    double _double;
    
    @FloatValue("_float")
    float _float;
    
    @StringValue("_string")
    String _string;
    
    @CharSequenceValue("_CharSequence")
    CharSequence _CharSequence;

    @IntArrayValue("_IntArrayValue")
    int[] _IntArrayValue;
    
    @BooleanArrayValue(value = "_BooleanArrayValue")
    boolean[] _BooleanArrayValue;
    
    @CharArrayValue("_CharArrayValue")
    char[] _CharArrayValue;

    @CharSequenceArrayValue("_CharSequenceArrayValue")
    CharSequence[] _CharSequenceArrayValue;

    @DoubleArrayValue("_DoubleArrayValue")
    double[] _DoubleArrayValue;
    
    @FloatArrayValue("_FloatArrayValue")
    float[] _FloatArrayValue;

    @ShortArrayValue("_ShortArrayValue")
    short[] _ShortArrayValue;

    @ByteArrayValue("_ByteArrayValue")
    byte[] _ByteArrayValue;

    @LongArrayValue("_LongArrayValue")
    long[] _LongArrayValue;
    
    @ParcelableArrayValue("_ParcelableArrayValue")
    Parcelable[] _ParcelableArrayValue;

    @SparseParcelableArrayValue("_SparseParcelableArrayValue")
    SparseArray<Bundle> _SparseParcelableArrayValue;

    @StringArrayValue("_StringArrayValue")
    String[] _StringArrayValue;

    @CharSequenceArrayListValue("_CharSequenceArrayListValue")
    List<CharSequence> _CharSequenceArrayListValue;

    @IntegerArrayListValue("_IntegerArrayListValue")
    List<Integer> _IntegerArrayListValue;

    @ParcelableArrayListValue("_ParcelableArrayListValue")
    List<Parcelable> _ParcelableArrayListValue;

    @StringArrayListValue("_StringArrayListValue")
    ArrayList<String> _StringArrayListValue;

    @ParcelableValue("_ParcelableValue")
    Bundle _ParcelableValue;

    @Required
    @SerializableValue("_SerializableValue")
    Map<String, Short> _SerializableValue;
}
```

#### how to bind fields from bundle

```java
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
    Bundle getLogin(@Required @StringValue("loginName") String loginName,
                    @Required @StringValue("password") String password);

    @BundleFlag(1)
    Bundle getInt(@IntValue("_int") int value);

    @BundleFlag(2)
    Bundle getString(@StringValue("_String") String value);

    @BundleFlag(3)
    Bundle getIntArray(@IntArrayValue("_intArray") int[] value);

    Bundle getStringArray(@StringArrayValue("_StringArray") String[] value);

    Bundle getParcelableArray(@ParcelableArrayValue("_ParcelableArray") Parcelable[] value);

    Bundle getSparseParcelableArray(@Required @SparseParcelableArrayValue("_SparseParcelableArray")
                                            SparseArray<? extends Parcelable> value);

    Bundle getStringArrayList(@Required @StringArrayListValue("_StringArrayListValue") ArrayList<String> value);

    Bundle getParcelableArrayList(@ParcelableArrayListValue("_ParcelableArrayList") ArrayList<? extends Parcelable> value);

    Bundle getParcelable(@ParcelableValue("_ParcelableArrayList") Parcelable value);

    Bundle getSerializable(@SerializableValue("_SerializableValue") Serializable value);

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
    implementation 'com.xcheng:autobundle-api:1.0.0'
    annotationProcessor 'com.xcheng:autobundle-compiler:1.0.0'
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
