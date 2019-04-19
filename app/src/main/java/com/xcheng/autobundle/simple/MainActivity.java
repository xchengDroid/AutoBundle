package com.xcheng.autobundle.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import autobundle.AutoBundle;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void invoke(View view) {
        Bundle bundle = AutoBundle.getDefault()
                .create(BundleService.class)
                //.testError(view);
                .getLogin("JackWharton", "123456");
        //.getInt(1);
        //.getSparseParcelableArray(sparseArray);
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
