package com.lemon.gmail.bootuserserviceprovider.service.impl;


import com.lemon.gmail.pojo.UserAddress;
import com.lemon.gmail.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-09 16:22
 **/
@com.alibaba.dubbo.config.annotation.Service
@Service
public class UserServiceImpl implements UserService {
    public List<UserAddress> getUserAddressList(String userId) {

        UserAddress address1 = new UserAddress(1, "河南省郑州巩义市宋陵大厦2F", "1", "安然", "150360313x", "Y");
        UserAddress address2 = new UserAddress(2, "北京市昌平区沙河镇沙阳路", "1", "情话", "1766666395x", "N");

        return Arrays.asList(address1,address2);
    }
}
