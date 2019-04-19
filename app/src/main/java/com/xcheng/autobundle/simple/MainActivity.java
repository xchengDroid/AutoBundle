package com.xcheng.autobundle.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import autobundle.AutoBundle;
import autobundle.annotation.Unbox;

public class MainActivity extends AppCompatActivity {
    @Unbox("name")
    String name;
    @Unbox("id")
    String id;
    @Unbox("address")
    String address;
    @Unbox("age")
    int age;
    @Unbox("sex")
    boolean sex;
    @Unbox("height")
    float height;
    @Unbox("weight")
    float weight;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoBundle.getDefault().bind(this);
    }
}
//        Bundle bundle = getIntent().getExtras();
//        name = bundle.getString("name");
//        id = bundle.getString("id");
//        address = bundle.getString("address");
//        age = bundle.getInt("age");
//        sex = bundle.getBoolean("sex", false);
//        height = bundle.getFloat("height");
//        weight = bundle.getFloat("weight");
//
//
//        Bundle personBundle = new Bundle();
//        personBundle.putString("name", "zhangsan");
//        personBundle.putString("id", "ABCD123456");
//        personBundle.putInt("age", 25);
//        personBundle.putBoolean("sex", false);
//        personBundle.putString("address", "中国浙江省");
//        personBundle.putFloat("height", 174.5f);
//        personBundle.putFloat("weight", 70);
//
//
//        String name = personBundle.getString("name");
//        String id = personBundle.getString("id");
//        String address = personBundle.getString("address");
//        int age = personBundle.getInt("age");
//        boolean sex = personBundle.getBoolean("sex");
//        float height = personBundle.getFloat("height");
//        float weight = personBundle.getFloat("weight");
//
//
//        personBundle = AutoBundle.getInstance()
//                .create(BundleService.class)
//                .getPersonInfo("zhangsan", "ABCD123456", 25, false, "中国浙江省", 174.5f, 70);
//
//    }
//
//
//    public void invoke(View view) {
//        Bundle bundle = AutoBundle.getInstance()
//                .create(BundleService.class)
//                .testError(view);
//        //.getLogin("JackWharton", "123456");
//        //.getInt(1);
//        //.getSparseParcelableArray(sparseArray);
//        Intent intent = new Intent(this, SecondActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
//}
