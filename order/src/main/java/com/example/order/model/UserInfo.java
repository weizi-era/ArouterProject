package com.example.order.model;

import com.example.common.user.BaseUser;

public class UserInfo extends BaseUser {

    private String token;
    private int vipLevel;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }
}
