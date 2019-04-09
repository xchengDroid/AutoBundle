package com.xcheng.autobundle.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import autobundle.annotation.IntValue;

public class TopActivity extends AppCompatActivity {

    @IntValue("121")
    int sex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static void main(String[] args) {
    }

}
