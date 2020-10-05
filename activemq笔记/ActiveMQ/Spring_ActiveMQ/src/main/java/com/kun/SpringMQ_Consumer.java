package com.kun;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description: <p>队列消费者</p>
 * @author: hounaikun
 * @create: 2020-10-04 16:34
 **/
@Service
public class SpringMQ_Consumer {
    @Resource
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        SpringMQ_Consumer consumer = applicationContext.getBean("springMQ_Consumer", SpringMQ_Consumer.class);
        String retValue = (String) consumer.jmsTemplate.receiveAndConvert();
        System.out.println("**********消费者收到的消息："+ retValue);
    }
}
