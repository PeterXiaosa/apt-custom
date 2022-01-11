package com.peter.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.peter.anno.BindView;

@BindView("routerMain")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void fun() {

    }
}