package com.example.arouter_api;

import java.util.Map;

public interface ARouterGroup {

    /**
     * todo key: app
     *      value: ARouterPath
     * @return map
     */
    Map<String, Class<? extends ARouterPath>> getGroupMap();
}
