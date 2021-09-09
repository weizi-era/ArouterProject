package com.example.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.arouter_annotation.ARouter;
import com.example.arouter_annotation.Parameter;
import com.example.arouter_api.ParameterManager;

@ARouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends AppCompatActivity {

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

        Log.d("TAG", "order传递的参数: name:" + name + ",sex:" + sex + ",age:" + age);
    }

}