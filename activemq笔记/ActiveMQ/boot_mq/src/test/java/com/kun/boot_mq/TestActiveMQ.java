package com.kun.boot_mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-05 20:46
 **/
//加载主类
@SpringBootTest(classes = BootMqApplication.class)
//加载spring的junit，如果不能用，把spring-boot-starter-test中的exclusion删除
@RunWith(SpringJUnit4ClassRunner.class)
//加载web
@WebAppConfiguration
public class TestActiveMQ {
    @Resource
    private Queue_Produce queue_produce;
    @Test
    public void testSend(){
        queue_produce.produceMsg();
    }
}
