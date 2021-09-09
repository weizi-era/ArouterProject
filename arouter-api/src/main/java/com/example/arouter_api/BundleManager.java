package com.example.arouter_api;

import android.content.Context;
import android.os.Bundle;

/**
 * 管理参数
 */
public class BundleManager {

    private Bundle bundle = new Bundle();

    public Bundle getBundle() {
        return bundle;
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

    // 直接完成跳转
    public void navigation(Context context) {
        ARouterManager.getInstance().navigation(context, this);
    }

}
