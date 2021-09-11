package com.example.order.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NetworkApi {

    @POST("/ip/ipNew")
    @FormUrlEncoded
    Call<ResponseBody> get(@Field("ip") String ip, @Field("key") String key);
}
