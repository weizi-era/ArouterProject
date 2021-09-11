package com.example.common.order;

import com.example.arouter_api.Call;

import java.io.IOException;

public interface OrderAddress extends Call {

    OrderBean getOrderBean(String key, String ip) throws IOException;
}
