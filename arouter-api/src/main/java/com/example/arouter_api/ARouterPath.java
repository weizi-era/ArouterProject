package com.example.arouter_api;

import com.example.arouter_annotation.bean.ARouterBean;

import java.util.Map;

public interface ARouterPath {

    /**
     * todo key: /app/MainActivity
     *      value: ARouterBean(MainActivity.class)
     * @return map
     */
    Map<String, ARouterBean> getPathMap();
}
