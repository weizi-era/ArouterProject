package com.example.arouter_api;

import android.app.Activity;
import android.content.Context;
import android.util.LruCache;

/**
 *  参数的加载管理器
 */
public class ParameterManager {

    private static ParameterManager instance;

    public static ParameterManager getInstance() {
        if (instance == null) {
            synchronized (ParameterManager.class) {
                if (instance == null) {
                    instance = new ParameterManager();
                }
            }
        }
        return instance;
    }

    /**
     * todo  key: 类名
     *       value：参数加载接口
     */
    private LruCache<String, ParameterGet> cache;

    public ParameterManager() {
        cache = new LruCache<>(100);
    }

    static final String FILE_SUFFIX_NAME = "$$Parameter";

    public void loadParameter(Activity activity) {
        String className = activity.getClass().getName();

        // 先从缓存里面拿
        ParameterGet parameterGet = cache.get(className);

        if (parameterGet == null) {
            try {
                Class<?> aClass = Class.forName(className + FILE_SUFFIX_NAME);
                parameterGet = (ParameterGet) aClass.newInstance();
                cache.put(className, parameterGet);
                parameterGet.getParameter(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
