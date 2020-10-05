package com.kun.boot_mq.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-04 17:47
 **/
@Component
public class ConfigBean {
    @Value("${myqueue}")
    private String myQueue;

    @Bean //bean id="" class = ""
    public Queue queue(){
        return new ActiveMQQueue(myQueue);
    }
}
