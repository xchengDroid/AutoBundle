package com.xcheng.autobundle.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import autobundle.AutoBundle;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = new Bundle();
        AutoBundle.getInstance().bind(this, new Bundle());
        ArrayList<? extends Bundle> values = new ArrayList<>();
        bundle.putParcelableArrayList("1212", values);
        String[] strings = new String[10];
        bundle.putCharSequenceArray("1232", strings);


//        ArrayList<? extends CharSequence> stringList = new ArrayList<>();
//        bundle.putCharSequenceArrayList("strings", stringList);
//
//        bundle.putParcelableArrayList("", new ArrayList<MyList>());

    }


    public void invoke(View view) {
        Bundle bundle = AutoBundle.getInstance()
                .create(BundleService.class)
                .getLogin("JackWharton","123456");
        //.getInt(1);
        //.getSparseParcelableArray(sparseArray);
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
