package com.example.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.arouter_annotation.ARouter;
import com.example.arouter_annotation.Parameter;
import com.example.arouter_api.ParameterManager;
import com.example.common.bean.Student;
import com.example.common.order.OrderAddress;
import com.example.common.order.OrderBean;
import com.example.common.order.OrderDrawable;

import java.io.IOException;

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

    @Parameter(name = "/order/getOrderBean")
    OrderAddress orderAddress;

    @Parameter
    Student student;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OrderBean orderBean = orderAddress.getOrderBean("aa205eeb45aa76c6afe3c52151b52160", "144.34.161.97");
                    Log.d("TAG", "run: " + orderBean.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //输出Student

        Log.d("TAG", "onCreate: Student == " + student.toString());
    }

}