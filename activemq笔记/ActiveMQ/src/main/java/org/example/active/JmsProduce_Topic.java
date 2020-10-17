package org.example.active;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-28 09:18
 **/
public class JmsProduce_Topic {
    public static final String ACTIVEMQ_URL = "tcp://121.199.70.188:61616";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = null;
        try {
            connection = activeMQConnectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            MessageProducer producer = session.createProducer(topic);
            for (int i = 0; i < 3; i++) {
                //7. 创建消息
                TextMessage textMessage = session.createTextMessage("message" + i);
                //8. 通过producer发送给MQ
                producer.send(textMessage);
            }
            producer.close();
            session.close();
            connection.close();
            System.out.println("消息发送到MQ成功！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
