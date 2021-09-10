package com.example.arouterproject;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.arouter_annotation.ARouter;
import com.example.arouter_annotation.Parameter;
import com.example.arouter_api.ARouterManager;
import com.example.arouter_api.ParameterManager;
import com.example.common.order.OrderDrawable;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Parameter(name = "/order/getDrawable")
    OrderDrawable orderDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParameterManager.getInstance().loadParameter(this);

        // 拿到order模块的图片 在app模块展示
        int drawable = orderDrawable.getDrawable();
        ImageView imageView = findViewById(R.id.img);
        imageView.setImageResource(drawable);

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