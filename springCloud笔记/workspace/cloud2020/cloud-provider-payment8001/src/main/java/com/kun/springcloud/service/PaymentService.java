package com.kun.springcloud.service;

import com.kun.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Param;


public interface PaymentService {
    int create(Payment payment);
    Payment getPaymentById(@Param("id") Integer id);
}
