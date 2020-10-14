package com.lemon.gmail.bootorderserviceconsumer.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lemon.gmail.pojo.UserAddress;
import com.lemon.gmail.service.OrderService;
import com.lemon.gmail.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-09 16:31
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Reference//引用远程提供者服务
    private UserService userService;

    public List<UserAddress> initOrder(String userID) {
        //查询用户的收货地址
        List<UserAddress> userAddressList = userService.getUserAddressList(userID);

        System.out.println("当前接收到的userId=> "+userID);
        System.out.println("**********");
        System.out.println("查询到的所有地址为：");
        for (UserAddress userAddress : userAddressList) {
            //打印远程服务地址的信息
            System.out.println(userAddress.getUserAddress());
        }
        return userAddressList;
    }
}
