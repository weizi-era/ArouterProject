package com.example.order.impl;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.arouter_annotation.ARouter;
import com.example.common.order.OrderAddress;
import com.example.common.order.OrderBean;
import com.example.order.api.NetworkApi;


import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@ARouter(path = "/order/getOrderBean")
public class OrderAddressImpl implements OrderAddress {

    private final static String BASE_URL = "http://apis.juhe.cn/";
    @Override
    public OrderBean getOrderBean(String key, String ip) throws IOException {
        Response<ResponseBody> response = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build()
                .create(NetworkApi.class)
                .get(ip, key)
                .execute();

        if (response.body() != null) {
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            OrderBean orderBean = jsonObject.toJavaObject(OrderBean.class);
            Log.d("TAG", "getOrderBean: " + orderBean.toString());
            return orderBean;
        }

        return null;
    }
}
