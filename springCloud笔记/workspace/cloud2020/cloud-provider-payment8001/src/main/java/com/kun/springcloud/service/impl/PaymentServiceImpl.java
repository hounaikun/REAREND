package com.kun.springcloud.service.impl;

import com.kun.springcloud.dao.PaymentDao;
import com.kun.springcloud.entities.Payment;
import com.kun.springcloud.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description: <p>支付业务逻辑</p>
 * @author: hounaikun
 * @create: 2020-11-04 23:37
 **/
@Service
public class PaymentServiceImpl implements PaymentService {
    @Resource
    private PaymentDao paymentDao;

    @Override
    public int create(Payment payment) {
        return paymentDao.create(payment);
    }

    @Override
    public Payment getPaymentById(Integer id) {
        return paymentDao.getPaymentById(id);
    }

}
