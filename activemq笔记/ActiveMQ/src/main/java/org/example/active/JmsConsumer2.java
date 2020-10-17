package org.example.active;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-25 09:33
 **/
public class JmsConsumer2 {
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
            //5. 创建消息的消费者
            MessageConsumer consumer = session.createConsumer(queue);
            //6. 消费者接收消息
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if(message != null && message instanceof TextMessage){
                        TextMessage textMessage = (TextMessage) message;
                        try {
                            System.out.println("***消费者接收到消息：" + textMessage.getText());
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            System.in.read(); //使控制台不关闭，等待输入，使下面的代码不立即执行
            //9. 关闭资源
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
