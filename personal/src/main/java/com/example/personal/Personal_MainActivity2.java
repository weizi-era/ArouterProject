package com.example.personal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arouter_annotation.ARouter;

@ARouter(path = "/personal/Personal_MainActivity2")
public class Personal_MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_main2);
    }
}