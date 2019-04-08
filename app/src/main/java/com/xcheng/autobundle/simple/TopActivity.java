package com.xcheng.autobundle.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import autobundle.annotation.BindBundle;
import autobundle.annotation.IntValue;

public class TopActivity extends AppCompatActivity {
    @BindBundle("12112112122")
    List<String> list;
    //    @BindBundle("1212")
//    Map<String, String> map;
//    @BindBundle("12121212")
    @IntValue("121")
    int sex;
  

//    @BindBundle(value = "12112122",name = "1212")
//    Map<String, ?> data2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Bundle bundle = AutoBundle.create(BundleFactory.class)
//                .login("12", "1212");

    }

    public static void main(String[] args) {
    }

}
