package com.example.arouter_api;

import android.content.Context;
import android.os.Bundle;

import java.io.Serializable;

/**
 * 管理参数
 */
public class BundleManager {

    private Call call;

    private Bundle bundle = new Bundle();

    public Bundle getBundle() {
        return bundle;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }


    public BundleManager writeString(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager writeBoolean(String key, Boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager writeInt(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager writeBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public BundleManager writeSerializable(String key, Serializable object) {
        bundle.putSerializable(key, object);
        return this;
    }

    // 直接完成跳转
    public Object navigation(Context context) {
       return ARouterManager.getInstance().navigation(context, this);
    }

}
