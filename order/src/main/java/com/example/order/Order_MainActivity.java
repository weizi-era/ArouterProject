package com.example.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.arouter_annotation.ARouter;
import com.example.arouter_annotation.Parameter;
import com.example.arouter_api.ARouterManager;
import com.example.arouter_api.ParameterManager;

@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends AppCompatActivity {

    @Parameter
    String name;

    @Parameter
    String sex;

    @Parameter
    int age = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);

        ParameterManager.getInstance().loadParameter(this);

        Log.d("TAG", "路由传过来的name: " + name + " sex:" + sex + " age:" + age);
    }

    public void jumpPersonal(View view) {
        ARouterManager.getInstance()
                .build("/personal/Personal_MainActivity")
                .writeString("name", "李大三")
                .writeString("sex", "女")
                .writeInt("age", 30)
                .navigation(this);
    }
}