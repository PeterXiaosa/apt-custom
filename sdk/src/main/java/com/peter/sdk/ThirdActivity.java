package com.peter.sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.peter.anno.BindView;

@BindView("routerThird")
public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
    }
}