package org.example.active;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-24 22:08
 **/
public class JmsProduce {
    //defaultURL = "tcp://" + DEFAULT_BROKER_HOST + ":" + DEFAULT_BROKER_PORT; 模板
    public static final String ACTIVEMQ_URL = "tcp://121.199.70.188:61616";
    public static final String QUEUE_NAME = "queue01";
    public static void main(String[] args) {
        //1. 创建连接工厂,按照给定的url地址，采用默认的用户名和密码
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = null;
        try {
            //2. 通过连接工厂，获得连接connection并启动访问
            connection = activeMQConnectionFactory.createConnection();
            connection.start();
            //3. 创建会话session
            //两个参数，第一个叫事务，第二个叫签收
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //4.创建目的地（具体是队列还是主题topic）
            Queue queue = session.createQueue(QUEUE_NAME);
            //5. 创建消息的生产者
            MessageProducer producer = session.createProducer(queue);
            //6. 通过消息生产者producer生产三条消息发送的MQ的队列里面
            for (int i = 0; i < 3; i++) {
                //7. 创建消息
                TextMessage textMessage = session.createTextMessage("message" + i);
                //8. 通过producer发送给MQ
                producer.send(textMessage);
            }
            //9. 关闭资源
            producer.close();
            session.close();
            connection.close();
            System.out.println("消息发送到MQ成功！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
