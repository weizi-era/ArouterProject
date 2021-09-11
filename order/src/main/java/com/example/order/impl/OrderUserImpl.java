package com.example.order.impl;

import com.example.arouter_annotation.ARouter;
import com.example.common.user.IUser;
import com.example.common.user.BaseUser;
import com.example.order.model.UserInfo;

@ARouter(path = "/order/getUserInfo")
public class OrderUserImpl implements IUser {
    @Override
    public BaseUser getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("zhaojiawei");
        userInfo.setAccount("admin");
        userInfo.setPassword("123456");
        userInfo.setVipLevel(9);
        userInfo.setPhoneNumber("18888888888");
        return userInfo;
    }
}
