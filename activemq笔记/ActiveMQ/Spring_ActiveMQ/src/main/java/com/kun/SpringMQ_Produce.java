package com.kun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @description: <p>队列生产者</p>
 * @author: hounaikun
 * @create: 2020-10-04 16:26
 **/
@Service
public class SpringMQ_Produce {
    @Resource(name = "jmsTemplate")
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        SpringMQ_Produce produce = applicationContext.getBean("springMQ_Produce", SpringMQ_Produce.class);
        produce.jmsTemplate.send((Session session) -> {
            TextMessage textMessage = session.createTextMessage("***spring和ActiveMQ的整合***");
            return  textMessage;
        });
        System.out.println("********send task over");
    }
}

