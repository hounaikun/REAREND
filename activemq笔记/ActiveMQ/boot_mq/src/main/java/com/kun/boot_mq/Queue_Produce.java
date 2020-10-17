package com.kun.boot_mq;

import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Queue;
import java.util.UUID;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-05 20:15
 **/
@Component
public class Queue_Produce {
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;
    //注入目的地
    @Resource
    private Queue queue;

    public void produceMsg(){
        jmsMessagingTemplate.convertAndSend(queue,"*******："+ UUID.randomUUID().toString().substring(0,6));
    }

    //定时任务，每3秒执行一次。
    @Scheduled(fixedDelay = 3000)
    public void produceMessageScheduled(){
        produceMsg();
    }
}
