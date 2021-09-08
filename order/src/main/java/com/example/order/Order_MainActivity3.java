package com.example.order;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arouter_annotation.ARouter;

@ARouter(path = "/order/Order_MainActivity3")
public class Order_MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main3);
    }
}