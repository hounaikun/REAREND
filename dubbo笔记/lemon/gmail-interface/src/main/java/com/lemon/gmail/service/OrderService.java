package com.lemon.gmail.service;

import com.lemon.gmail.pojo.UserAddress;

import java.util.List;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-09 16:30
 **/
public interface OrderService {
    /**
     * 初始化订单
     * @param userID
     */
    List<UserAddress> initOrder(String userID);
}
