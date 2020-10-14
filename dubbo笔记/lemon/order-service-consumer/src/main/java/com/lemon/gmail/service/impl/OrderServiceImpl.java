package com.lemon.gmail.service.impl;

import com.lemon.gmail.pojo.UserAddress;
import com.lemon.gmail.service.OrderService;
import com.lemon.gmail.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-09 16:31
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    public UserService userService;
    public List<UserAddress> initOrder(String userID) {
        //查询用户的收货地址
        List<UserAddress> userAddressList = userService.getUserAddressList(userID);
        //为了直观的看到得到的数据，以下内容也可不写
        System.out.println("当前接收到的userId=> "+userID);
        System.out.println("**********");
        System.out.println("查询到的所有地址为：");
        for (UserAddress userAddress : userAddressList) {
            //打印远程服务地址的信息
            System.out.println(userAddress.getUserAddress());
        }
        return null;
    }
}
