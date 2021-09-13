package com.example.arouterproject;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arouter_annotation.ARouter;
import com.example.arouter_annotation.Parameter;
import com.example.arouter_api.ARouterManager;
import com.example.arouter_api.ParameterManager;
import com.example.common.bean.Student;
import com.example.common.order.OrderDrawable;
import com.example.common.user.IUser;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Parameter(name = "/order/getDrawable")
    OrderDrawable orderDrawable;

    @Parameter(name = "/order/getUserInfo")
    IUser iUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionX.init(this)
                .permissions(Manifest.permission.INTERNET)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            Toast.makeText(MainActivity.this, "All permissions are granted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "These permissions are denied: " + deniedList, Toast.LENGTH_LONG).show();
                        }
                    }
                });

        ParameterManager.getInstance().loadParameter(this);

        // 拿到order模块的图片 在app模块展示
        int drawable = orderDrawable.getDrawable();
        ImageView imageView = findViewById(R.id.img);
        imageView.setImageResource(drawable);

        Log.d("TAG", "order UserInfo ==: " + iUser.getUserInfo().toString());
    }

    public void jumpOrder(View view) {

        ARouterManager.getInstance()
                .build("/order/Order_MainActivity")
                .writeString("name", "王小二")
                .writeString("sex", "男")
                .writeInt("age", 28)
                .navigation(this);
    }

    public void jumpPersonal(View view) {

        Student student = new Student("zhaojiawei", "男", 28);

        ARouterManager.getInstance()
                .build("/personal/Personal_MainActivity")
                .writeSerializable("student", student)
                .navigation(this);
    }
}