package com.example.arouter_api;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.LruCache;

import com.example.arouter_annotation.bean.ARouterBean;

public class ARouterManager {

    private String group;
    private String path;

    private static ARouterManager instance;

    public static ARouterManager getInstance() {
        if (instance == null) {
            synchronized (ARouterManager.class) {
                if (instance == null) {
                    instance = new ARouterManager();
                }
            }
        }
        return instance;
    }

    private final static String FILE_GROUP_NAME = "ARouter$$Group$$";

    private LruCache<String, ARouterGroup> groupCache;
    private LruCache<String, ARouterPath> pathCache;

    public ARouterManager() {
        groupCache = new LruCache<>(100);
        pathCache = new LruCache<>(100);
    }

    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // 截取组名  /order/Order_MainActivity  finalGroup=order
        String finalGroup = path.substring(1, path.indexOf("/", 1)); // finalGroup = order

        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        this.path = path;
        this.group = finalGroup;

        return new BundleManager();
    }

    public Object navigation(Context context, BundleManager bundleManager) {

        String groupClassName = context.getPackageName() + "." + FILE_GROUP_NAME + group;

        try {
            // 1. 读取路由组Group类文件
            ARouterGroup loadGroup = groupCache.get(group);
            if (loadGroup == null) {
                Class<?> aClass = Class.forName(groupClassName);

                loadGroup = (ARouterGroup) aClass.newInstance();
                groupCache.put(group, loadGroup);
            }

            if (loadGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("路由表Group报废了..."); // Group这个类 加载失败
            }

            // 2. 读取路由Path类文件
            ARouterPath loadPath = pathCache.get(path);
            if (loadPath == null) {
                Class<? extends ARouterPath> aClass = loadGroup.getGroupMap().get(group);

                if (aClass != null) {
                    loadPath = aClass.newInstance();
                }

                pathCache.put(path, loadPath);
            }

            if (loadPath != null) {
                if (loadPath.getPathMap().isEmpty()) {
                    throw new RuntimeException("路由表Path报废了...");
                }

                ARouterBean routerBean = loadPath.getPathMap().get(path);
                if (routerBean != null) {
                    switch (routerBean.getTypeEnum()) {
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getaClass());
                            intent.putExtras(bundleManager.getBundle());
                            context.startActivity(intent);
                            break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
