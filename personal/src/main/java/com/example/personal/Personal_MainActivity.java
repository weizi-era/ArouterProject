package com.example.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.arouter_annotation.ARouter;
import com.example.arouter_annotation.Parameter;
import com.example.arouter_api.ParameterManager;
import com.example.common.order.OrderDrawable;

@ARouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends AppCompatActivity {

    @Parameter(name = "/order/getDrawable")
    OrderDrawable orderDrawable;

    @Parameter
    String name;

    @Parameter
    String sex;

    @Parameter
    int age = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_main);

        ParameterManager.getInstance().loadParameter(this);

        Log.d("TAG", "onCreate: " + orderDrawable);

        // 拿到order模块的图片 在personal模块展示
        int drawable = orderDrawable.getDrawable();
        ImageView imageView = findViewById(R.id.img);
        imageView.setImageResource(drawable);

        Log.d("TAG", "order传递的参数: name:" + name + ",sex:" + sex + ",age:" + age);
    }

}