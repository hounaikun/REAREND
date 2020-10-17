package com.lemon.gmail.service;

import com.lemon.gmail.pojo.UserAddress;

import java.util.List;

public interface UserService {
    /**
     * 按照用户id返回所有的收货地址
     * @param userId
     * @return
     */
    List<UserAddress> getUserAddressList(String userId);
}
