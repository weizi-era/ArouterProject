package com.example.arouterproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.arouter_annotation.ARouter;
import com.example.arouter_annotation.Parameter;
import com.example.arouter_api.ARouterManager;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpOrder(View view) {

        ARouterManager.getInstance()
                .build("/order/Order_MainActivity")
                .writeString("name", "王小二")
                .writeString("sex", "男")
                .writeInt("age", 28)
                .navigation(this);
    }
}