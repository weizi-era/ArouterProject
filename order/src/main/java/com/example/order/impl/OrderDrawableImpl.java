package com.example.order.impl;

import com.example.arouter_annotation.ARouter;
import com.example.common.order.OrderDrawable;
import com.example.order.R;

@ARouter(path = "/order/getDrawable")
public class OrderDrawableImpl implements OrderDrawable {
    @Override
    public int getDrawable() {
        return R.drawable.test;
    }
}
