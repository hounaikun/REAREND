package com.kun.springcloud.controller;

import com.kun.springcloud.entities.CommonResult;
import com.kun.springcloud.entities.Payment;
import com.kun.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-11-04 23:40
 **/
@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @PostMapping("/create")
    public CommonResult<Payment> create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("*****插入结果：" + result);

        if(result > 0){
            return new CommonResult(200,"插入数据库成功,serverPort："+serverPort,result);
        }else{
            return new CommonResult(444,"插入数据库失败");
        }
    }
    @GetMapping("/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") int id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("*****插入结果 ：" + payment);
        if(payment != null){
            return new CommonResult(200,"查询数据成功,serverPort："+serverPort,payment);
        }else{
            return new CommonResult(444,"没有对应记录，查询ID: "+ id);
        }
    }

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout(){
        try { TimeUnit.SECONDS.sleep(3); }catch (Exception e) {e.printStackTrace();}
        return serverPort;
    }
}
