## 消息中间件之ActiveMQ-基础篇
*注：该博客参考  尚硅谷周阳老师，仅供个人学习使用，转载请标明作者和此段文字*
### 一、消息中间件是什么?
> MQ = 消息中间件
#### 1. MQ产品总类
> kafka、RabbitMQ、RocketMQ、ActiveMQ
#### 2. 是什么
> 利用可靠的消息传递机制进行与平台无关的数据交流，并基于数据通信来进行分布式系统的集成。
> 通过提供**消息传递**和**消息排队**模型在分布式环境下提供应用解耦、弹性伸缩、冗余存储、流量削峰、异步通信、数据同步等功能。
#### 3. 能干吗
> 解耦、削峰、异步
#### 4. 过程
> 发送者把消息发送给消息服务器，消息服务器将消息存放在若干**队列/主题**中，在合适的时候，消息服务器会将消息转发给接收者。在这个过程中，**发送和接受是异步的**，也就是发送无需等待，而且发送者和接收者的生命周期也没有必然关系。
> - 什么是异步？
>   &emsp;&emsp;消息发送者可以发送一个消息而无需等待响应。消息发送者将消息发送到一条虚拟的通道（主题或队列）上；消息接收者则订阅或监听该通道。一条消息可能最终转发给一个或多个消息接收者，这些消息接收者都无需对消息发送者做出同步回应。整个过程都是异步的。
> - 什么是系统之间解耦?
>   &emsp;&emsp;发送者和接收者不必了解对方，只需要确认消息；发送者和接收者不必同时在线。
#### 5. 去哪下
> ActiveMq官网
> [http://activemq.apache.org](http://activemq.apache.org)
### 二、安装
#### 1. 安装步骤
> 1. 官网下载
> 2. 解压缩
#### 2. 启动
- 普通启动
```bash
cd activemq解压包/bin
./activemq start  #默认进程端口号61616
```
- 关闭
 ```bash
cd activemq解压包/bin
./activemq stop 
 ```
- 重启
```bash
cd activemq解压包/bin
./activemq restart  #默认进程端口号61616(后台端口号)
```
- 带日志的启动
```bash
./activemq start > /目录/run_activemq.log #将相关日志写入run_activemq.log文件中
```
#### 3. 访问
```bash
http://ip:8161/admin  #默认前台端口号8161
# 默认的用户名和密码是admin/admin
/*如果访问不了？
1. 查看云服务器是否将8161和61616端口（61616端口可以不配置）加入开放
2. activemq 配置文件 jetty.xml 中的host 从127.0.0.1变成0.0.0.0
3. 查看防火墙是否开放8161
	systemctl status firewalld 查看防火墙状态（是否启动）
	firewall-cmd --list-port 查看防火墙开放的端口
	firewall-cmd --zone=public --add-port=61616/tcp --permanent 开放61616端口
	firewall-cmd --zone=public --add-port=8161/tcp --permanent 开放8161端口
	firewall-cmd --reload 使配置生效
	firewall-cmd --query-port=61616/tcp 查看61616是否开放
*/
```
> 备注：
> - **采用61616端口提供JMS服务**
> - **采用8161端口提供管理控制台服务**
### 三、java实现ActiveMQ通讯
> 1. IDEA创建Maven工程
> 2. POM.xml文件导入包
```xml
<!--activemq所需要的jar包-->
    <!-- https://mvnrepository.com/artifact/org.apache.activemq/activemq-all -->
    <dependency>
      <groupId>org.apache.activemq</groupId>
      <artifactId>activemq-all</artifactId>
      <version>5.15.9</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.xbean/xbean-spring -->
    <dependency>
      <groupId>org.apache.xbean</groupId>
      <artifactId>xbean-spring</artifactId>
      <version>3.16</version>
    </dependency>
```
> 3. JMS编码总体架构
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200924215449120.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 4. 粗说目的地Destination （队列和主题）
>   4.1 **在点对点的消息传递域中，目的地被称为队列（queue）**
>   4.2 **在发布订阅消息传递域中，目的地被称为主题（topic）**

> 5. java编码(队列)
```java
public class JmsProduce {
    //defaultURL = "tcp://" + DEFAULT_BROKER_HOST + ":" + DEFAULT_BROKER_PORT; 模板
    public static final String ACTIVEMQ_URL = "tcp://***:61616";
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
```

```java
/*消费者方法1
	同步阻塞方式(receive()):
		订阅者或者接收者调用MessageConsumer的receive()方法来接收消息，receive方法能够接收到消息之前（或超时之前）将一直阻塞。
*/
public class JmsConsumer {
    //defaultURL = "tcp://" + DEFAULT_BROKER_HOST + ":" + DEFAULT_BROKER_PORT; 模板
    public static final String ACTIVEMQ_URL = "tcp://***:61616";
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
            while(true){
                /*
                * receive() 死等
                * receive(long timeout) 等timeout时间后，消费者就走了*/
                TextMessage textMessage = (TextMessage) consumer.receive();
                if(null != textMessage){
                    System.out.println("***消费者接收到消息："+ textMessage.getText());
                }else{
                    break;
                }
            }
            //9. 关闭资源
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
```
```java
/*消费者方法2
	通过监听的方式来接收消息。
	异步非阻塞方式(监听器onMessage())
		订阅者或接受者通过MessageConsumer的setMessageListener(MessageListener listener)注册一个消息监听器，
		当消息到达之后，系统自动调用监听器MessageListener的onMessage(Message message)方法。
		
*/
public class JmsConsumer2 {
    //defaultURL = "tcp://" + DEFAULT_BROKER_HOST + ":" + DEFAULT_BROKER_PORT; 模板
    public static final String ACTIVEMQ_URL = "tcp://***:61616";
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
```
> - 控制台说明
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020092422322085.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

>6. 消费者3消费情况（队列）
>  6.1 先生产，只启动1号消费者。问题：1号消费者能消费消息吗？
>  &emsp;&emsp;可以
>  6.2 先生产，先启动1号消费者，再启动2号消费者。问题：2号消费者能消费消息吗？
>  &emsp;&emsp;1号可以消费；2号消费者不可以消费，因为已经被1号消费完。
>  6.3 先启动两个消费者，再生产6条消息，请问，消费情况如何？
>  &emsp;&emsp;一人一半。

> 7. 发布订阅者消息传递域的特点(主题)：
>   &emsp;&emsp;(1)生产者将消息发布到topic中，每个消息可以有多个消费者，属于1:N的关系。
>   &emsp;&emsp;(2)生产者和消费者之间有**时间上**的相关性。订阅某一个主题的消费者只能消费**自它订阅之后发布的消息**。
>   &emsp;&emsp;(3)生产者生产时，topic **不保存消息**，它是无状态的，假如无人订阅就去生产，那就是一条废消息，所以，一般**先启动消费者再启动生产者**。
>   JMS规范允许客户创建持久订阅，这在一定程度上发送了时间上的相关要求。持久订阅允许消费者消费它在未处于激活状态时发送的消息。**一句话，好比微信公众号订阅**
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200928091943711.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 8. java代码(主题)
```java
//消费者
public class JmsConsumer_Topic {
    //defaultURL = "tcp://" + DEFAULT_BROKER_HOST + ":" + DEFAULT_BROKER_PORT; 模板
    public static final String ACTIVEMQ_URL = "tcp://***:61616";
    public static final String TOPIC_NAME = "topic01";

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
            Topic topic = session.createTopic(TOPIC_NAME);
            //5. 创建消息的消费者
            MessageConsumer consumer = session.createConsumer(topic);
            //6. 消费者接收消息
            while (true) {
                /*
                 * receive() 死等
                 * receive(long timeout) 等timeout时间后，消费者就走了*/
                TextMessage textMessage = (TextMessage) consumer.receive();
                if (null != textMessage) {
                    System.out.println("***消费者接收到消息：" + textMessage.getText());
                } else {
                    break;
                }
            }
            //9. 关闭资源
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
```
```java
//生产者
public class JmsProduce_Topic {
    public static final String ACTIVEMQ_URL = "tcp://***:61616";
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
```
> 9. 两大模式对比
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200928214143329.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
### 四、JMS 规范和落地产品
> 1. javaEE 是什么？
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200928214817506.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

>2. JMS 是什么？
>  Java Message Service，Java消息服务是javaEE中的一个技术。Java消息服务指的是两个应用程序之问进行异步通信的APl,它为标准消息协议和消息服务提供了一组通用接口,包括创建、发送、读取消息等,用于支持JAVA应用程序开发。在 Javaee中,当两个应用程序使用JMS进行通信时,它们之间并不是直接相连的,而是通过一个共同的消息收发服务件关联起来以达到解耦/异步/削峰的效果。
>  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200928215404109.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

>3. MQ中间件其它落地产品？
>  ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020092821562051.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
>  对比？

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200928220749665.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

>4. JMS的组成结构和特点
>  ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020092822120072.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 4.1 消息头
```java
JMS的消息头有哪些属性：
	JMSDestination：消息目的地
	JMSDeliveryMode：消息持久化模式
	JMSExpiration：消息过期时间
	JMSPriority：消息的优先级
	JMSMessageID：消息的唯一标识符。后面我们会介绍如何解决幂等性。
说明：消息的生产者可以set这些属性，消息的消费者可以get这些属性。
这些属性在send方法里面也可以设置。
package  com.at.activemq.topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class JmsProduce_topic {

    public static final String ACTIVEMQ_URL = "tcp://***:61626";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws  Exception{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        MessageProducer messageProducer = session.createProducer(topic);

        for (int i = 1; i < 4 ; i++) {
            TextMessage textMessage = session.createTextMessage("topic_name--" + i);
            // 这里可以指定每个消息的目的地
            textMessage.setJMSDestination(topic);
            /*
            持久模式和非持久模式。
            一条持久性的消息：应该被传送“一次仅仅一次”，这就意味着如果JMS提供者出现故障，该消息并不会丢失，它会在服务器恢复之后再次传递。
            一条非持久的消息：最多会传递一次，这意味着服务器出现故障，该消息将会永远丢失。
             */
            textMessage.setJMSDeliveryMode(0);
            /*
            可以设置消息在一定时间后过期，默认是永不过期。
            消息过期时间，等于Destination的send方法中的timeToLive值加上发送时刻的GMT时间值。
            如果timeToLive值等于0，则JMSExpiration被设为0，表示该消息永不过期。
            如果发送后，在消息过期时间之后还没有被发送到目的地，则该消息被清除。
             */
            textMessage.setJMSExpiration(1000);
            /*  消息优先级，从0-9十个级别，0-4是普通消息5-9是加急消息。
            JMS不要求MQ严格按照这十个优先级发送消息但必须保证加急消息要先于普通消息到达。默认是4级。
             */
            textMessage.setJMSPriority(10);
            // 唯一标识每个消息的标识。MQ会给我们默认生成一个，我们也可以自己指定。
            textMessage.setJMSMessageID("ABCD");
            // 上面有些属性在send方法里也能设置
            messageProducer.send(textMessage);
        }
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("  **** TOPIC_NAME消息发送到MQ完成 ****");
    }
}
```
> 4.2 消息体
> &emsp;&emsp;封装具体的消息数据，有5种消息体格式，发送和接收的消息体类型必须一致对应。
```java
/*
5种消息体格式：
	TextMessage 普通字符串消息，包含一个string
	MapMessage 一个Map类型的消息，key为string类型，而值为Java的基本类型
	ByteMessage 二进制数组消息，包含一个byte[]
	StreamMessage Java数据流消息，用标准流操作来顺序的填充和读取
	ObjectMessage 对象消息，包含一个可序列化的Java对象
*/
```
```java
//消息生产者
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class JmsProduce_topic {

    public static final String ACTIVEMQ_URL = "tcp://***:61626";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws  Exception{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
         javax.jms.Connection connection = activeMQConnectionFactory.createConnection();
         connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        MessageProducer messageProducer = session.createProducer(topic);

        for (int i = 1; i < 4 ; i++) {
// 发送TextMessage消息体
            TextMessage textMessage = session.createTextMessage("topic_name--" + i);
            messageProducer.send(textMessage);
            // 发送MapMessage  消息体。set方法: 添加，get方式：获取
            MapMessage  mapMessage = session.createMapMessage();
            mapMessage.setString("name", "张三"+i);
            mapMessage.setInt("age", 18+i);
            messageProducer.send(mapMessage);
        }
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("  **** TOPIC_NAME消息发送到MQ完成 ****");
    }
}
```
```java
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class JmsConsummer_topic {
    public static final String ACTIVEMQ_URL = "tcp://***:61626";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws Exception{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        javax.jms.Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        MessageConsumer messageConsumer = session.createConsumer(topic);

        messageConsumer.setMessageListener( (message) -> {
 // 判断消息是哪种类型之后，再强转。
            if (null != message  && message instanceof TextMessage){
                   TextMessage textMessage = (TextMessage)message;
                    try {
                      System.out.println("****消费者text的消息："+textMessage.getText());
                    }catch (JMSException e) {
                    }
                }
            if (null != message  && message instanceof MapMessage){
                MapMessage mapMessage = (MapMessage)message;
                try {
                    System.out.println("****消费者的map消息："+mapMessage.getString("name"));
                    System.out.println("****消费者的map消息："+mapMessage.getInt("age"));
                }catch (JMSException e) {
                }
            }
        });
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
```
> 4.3 消息属性
> &emsp;&emsp;如果需要除消息头字段之外的值，那么可以使用消息属性。他是**识别/去重/重点标注**等操作，非常有用的方法。
> 他们是以属性名和属性值对的形式制定的。可以将属性是为消息头得扩展，属性指定一些消息头没有包括的附加信息，比如可以在属性里指定消息选择器。消息的属性就像可以分配给一条消息的附加消息头一样。它们允许开发者添加有关消息的不透明附加信息。它们还用于暴露消息选择器在消息过滤时使用的数据。
> 下图是设置消息属性的API：
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200929090451521.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
```java
//消息生产者
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class JmsProduce_topic {

    public static final String ACTIVEMQ_URL = "tcp://***:61626";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws  Exception{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        MessageProducer messageProducer = session.createProducer(topic);

        for (int i = 1; i < 4 ; i++) {
            TextMessage textMessage = session.createTextMessage("topic_name--" + i);
            // 调用Message的set*Property()方法，就能设置消息属性。根据value的数据类型的不同，有相应的API。
            textMessage.setStringProperty("From","ZhangSan@qq.com");
            textMessage.setByteProperty("Spec", (byte) 1);
            textMessage.setBooleanProperty("Invalide",true);
            messageProducer.send(textMessage);
        }
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("  **** TOPIC_NAME消息发送到MQ完成 ****");
    }
}
```
```java
//消费者
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class JmsConsummer_topic {
    public static final String ACTIVEMQ_URL = "tcp://***:61626";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws Exception{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        javax.jms.Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        MessageConsumer messageConsumer = session.createConsumer(topic);

        messageConsumer.setMessageListener( (message) -> {
            if (null != message  && message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage)message;
                    try {
                      System.out.println("消息体："+textMessage.getText());
                      System.out.println("消息属性："+textMessage.getStringProperty("From"));
                      System.out.println("消息属性："+textMessage.getByteProperty("Spec"));
                      System.out.println("消息属性："+textMessage.getBooleanProperty("Invalide"));
                    }catch (JMSException e) {
                    }
                }
        });
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
```
5. 消息的持久化
> 什么是持久化消息？
> &emsp;&emsp;保证消息只被传送一次和成功使用一次。在持久性消息传送至目标时，消息服务将其放入持久性数据存储。如果消息服务由于某种原因导致失败，它可以恢复此消息并将此消息传送至相应的消费者。虽然这样增加了消息传送的开销，但却增加了可靠性。
> 理解：在消息生产者将消息成功发送给MQ消息中间件之后。无论是出现任何问题，如：MQ服务器宕机、消费者掉线等。都保证（topic要之前注册过，queue不用）消息消费者，能够成功消费消息。如果消息生产者发送消息就失败了，那么消费者也不会消费到该消息。

> 5.1 queue消息非持久和持久
> &emsp;&emsp;**queue非持久**，当服务器宕机，消息不存在（消息丢失了）。
> &emsp;&emsp;**queue持久化**，当服务器宕机，消息依然存在。queue消息默认是持久化的。
> &emsp;&emsp;**持久化消息**，**这是队列默认的传送方式**，保证这些消息只被传送一次和成功使用一次。对于这些消息，**可靠性**是优先考虑的因素。
> 可靠性的另一个重要方面是确保持久性消息传送至目标后，消息服务在向消费者传送它们之前不会丢失这些消息。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200929093146150.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 5.2 topic消息持久化
> &emsp;&emsp;topic默认就是非持久化的，因为生产者生产消息时，消费者也要在线，这样消费者才能消费到消息。
> &emsp;&emsp;topic消息持久化，只要消费者向MQ服务器注册过，所有生产者发布成功的消息，该消费者都能收到，不管是MQ服务器宕机还是消费者不在线。类似微信公众号订阅发布。
> 注意：
> - 一定要先运行一次消费者，等于向MQ注册，类似我订阅了这个主题。
> - 然后再运行生产者发送消息。
> - 之后无论消费者是否在线，都会收到消息。如果不在线的话，下次连接的时候，会把没有收过的消息都接收过来。
```java
//生产者
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

// 持久化topic 的消息生产者
public class JmsProduce_persistence {

    public static final String ACTIVEMQ_URL = "tcp://***:61616";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws  Exception{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        javax.jms.Connection connection = activeMQConnectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        MessageProducer messageProducer = session.createProducer(topic);

        // 设置持久化topic 
        messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
        // 设置持久化topic之后再，启动连接
        connection.start();
        for (int i = 1; i < 4 ; i++) {
            TextMessage textMessage = session.createTextMessage("topic_name--" + i);
            messageProducer.send(textMessage);
            MapMessage mapMessage = session.createMapMessage();
        }
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("  **** TOPIC_NAME消息发送到MQ完成 ****");
    }
}
```
```java
//消费者
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

// 持久化topic 的消息消费者
public class JmsConsummer_persistence {
    public static final String ACTIVEMQ_URL = "tcp://***:61616";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws Exception{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
// 设置客户端ID。向MQ服务器注册自己的名称
        connection.setClientID("marrry");
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
// 创建一个topic订阅者对象。一参是topic，二参是订阅者名称
        TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic,"remark...");
         // 之后再开启连接
        connection.start();
        Message message = topicSubscriber.receive();
         while (null != message){
             TextMessage textMessage = (TextMessage)message;
             System.out.println(" 收到的持久化 topic ："+textMessage.getText());
             message = topicSubscriber.receive();
         }
        session.close();
        connection.close();
    }
}
```
>控制台介绍：
>topic页面还是和之前的一样。另外在subscribers页面也会显示。如下：
>![在这里插入图片描述](https://img-blog.csdnimg.cn/20200929101143746.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
6. 消息的事务性
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200929211743274.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> (1)	生产者开启事务后，执行commit方法，这批消息才真正的被提交。不执行commit方法，这批消息不会提交。执行rollback方法，之前的消息会回滚掉。生产者的事务机制，要高于签收机制，当生产者开启事务，签收机制不再重要。
> (2)	消费者开启事务后，执行commit方法，这批消息才算真正的被消费。不执行commit方法，这些消息不会标记已消费，下次还会被消费。执行rollback方法，是不能回滚之前执行过的业务逻辑，但是能够回滚之前的消息，回滚后的消息，下次还会被消费。消费者利用commit和rollback方法，甚至能够违反一个消费者只能消费一次消息的原理。
> (3)	问：消费者和生产者需要同时操作事务才行吗？   
> 答：消费者和生产者的事务，完全没有关联，各自是各自的事务。
```java
//生产者
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Jms_TX_Producer {
    private static final String ACTIVEMQ_URL = "tcp://***:61616";
    private static final String ACTIVEMQ_QUEUE_NAME = "Queue-TX";

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        //1.创建会话session，两个参数transacted=事务,acknowledgeMode=确认模式(签收)
        //设置为开启事务
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageProducer producer = session.createProducer(queue);
        try {
            for (int i = 0; i < 3; i++) {
                TextMessage textMessage = session.createTextMessage("tx msg--" + i);
              producer.send(textMessage);
if(i == 2){
                    throw new RuntimeException("GG.....");
                }
            }
            // 2. 开启事务后，使用commit提交事务，这样这批消息才能真正的被提交。
            session.commit();
            System.out.println("消息发送完成");
        } catch (Exception e) {
            System.out.println("出现异常,消息回滚");
            // 3. 工作中一般，当代码出错，我们在catch代码块中回滚。这样这批发送的消息就能回滚。
            session.rollback();
        } finally {
            //4. 关闭资源
            producer.close();
            session.close();
            connection.close();
        }
    }
}
```
```java
//消费者
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.IOException;

public class Jms_TX_Consumer {
    private static final String ACTIVEMQ_URL = "tcp://***:61626";
    private static final String ACTIVEMQ_QUEUE_NAME = "Queue-TX";

    public static void main(String[] args) throws JMSException, IOException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        // 创建会话session，两个参数transacted=事务,acknowledgeMode=确认模式(签收)
        // 消费者开启了事务就必须手动提交，不然会重复消费消息
        final Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(new MessageListener() {
            int a = 0;
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    try {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("***消费者接收到的消息:   " + textMessage.getText());
                        if(a == 0){
                            System.out.println("commit");
                            session.commit();
                        }
                        if (a == 2) {
                            System.out.println("rollback");
                            session.rollback();
                        }
                        a++;
                    } catch (Exception e) {
                        System.out.println("出现异常，消费失败，放弃消费");
                        try {
                            session.rollback();
                        } catch (JMSException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        //关闭资源
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
```
7. 消息的签收机制
> 一、签收的几种方式
> ①　自动签收（Session.AUTO_ACKNOWLEDGE）：该方式是默认的。该种方式，无需我们程序做任何操作，框架会帮我们自动签收收到的消息。
> ②　手动签收（Session.CLIENT_ACKNOWLEDGE）：手动签收。该种方式，需要我们手动调用Message.acknowledge()，来签收消息。如果不签收消息，该消息会被我们反复消费，只到被签收。
> ③　允许重复消息（Session.DUPS_OK_ACKNOWLEDGE）：多线程或多个消费者同时消费到一个消息，因为线程不安全，可能会重复消费。该种方式很少使用到。
> ④　事务下的签收（Session.SESSION_TRANSACTED）：开始事务的情况下，可以使用该方式。该种方式很少使用到。

>二、事务和签收的关系
>①　在事务性会话中，当一个事务被成功提交则消息被自动签收。如果事务回滚，则消息会被再次传送。事务优先于签收，开始事务后，签收机制不再起任何作用。
>②　非事务性会话中，消息何时被确认取决于创建会话时的应答模式。
>③　生产者事务开启，只有commit后才能将全部消息变为已消费。
>④　事务偏向生产者，签收偏向消费者。也就是说，生产者使用事务更好点，消费者使用签收机制更好点。
```java
//生产者
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Jms_TX_Producer {

    private static final String ACTIVEMQ_URL = "tcp://***:61626";
    private static final String ACTIVEMQ_QUEUE_NAME = "Queue-ACK";

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageProducer producer = session.createProducer(queue);
        try {
            for (int i = 0; i < 3; i++) {
                TextMessage textMessage = session.createTextMessage("tx msg--" + i);
                producer.send(textMessage);
            }
            System.out.println("消息发送完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
            session.close();
            connection.close();
        }
    }
}
```
```java
//消费者
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.IOException;

public class Jms_TX_Consumer {
    private static final String ACTIVEMQ_URL = "tcp://***:61626";
    private static final String ACTIVEMQ_QUEUE_NAME = "Queue-ACK";

    public static void main(String[] args) throws JMSException, IOException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    try {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("***消费者接收到的消息:   " + textMessage.getText());
                        /* 设置为Session.CLIENT_ACKNOWLEDGE后，要调用该方法，标志着该消息已被签收（消费）。
                            如果不调用该方法，该消息的标志还是未消费，下次启动消费者或其他消费者还会收到改消息。
                         */
                        textMessage.acknowledge();
                    } catch (Exception e) {
                        System.out.println("出现异常，消费失败，放弃消费");
                    }
                }
            }
        });
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
```
8. JMS的点对点总结
> &emsp;点对点模型是基于队列的，生产者发消息到队列，消费者从队列接收消息，队列的存在使得消息的异步传输成为可能。和我们平时给朋友发送短信类似。
> &emsp;如果在Session关闭时有部分消息己被收到但还没有被签收(acknowledged),那当消费者下次连接到相同的队列时，这些消息还会被再次接收
> &emsp;队列可以长久地保存消息直到消费者收到消息。消费者不需要因为担心消息会丢失而时刻和队列保持激活的连接状态，充分体现了异步传输模式的优势
9. JMS的发布订阅总结
   (1)	JMS的发布订阅总结
   &emsp;JMS Pub/Sub 模型定义了如何向一个内容节点发布和订阅消息，这些节点被称作topic。
   &emsp;主题可以被认为是消息的传输中介，发布者（publisher）发布消息到主题，订阅者（subscribe）从主题订阅消息。
   &emsp;主题使得消息订阅者和消息发布者保持互相独立不需要解除即可保证消息的传送

   (2)	非持久订阅
   &emsp;非持久订阅只有当客户端处于激活状态，也就是和MQ保持连接状态才能收发到某个主题的消息。
   &emsp;如果消费者处于离线状态，生产者发送的主题消息将会丢失作废，消费者永远不会收到。
   &emsp;一句话：先订阅注册才能接受到发布，只给订阅者发布消息。

   (3)	持久订阅
   &emsp;客户端首先向MQ注册一个自己的身份ID识别号，当这个客户端处于离线时，生产者会为这个ID保存所有发送到主题的消息，当客户再次连接到MQ的时候，会根据消费者的ID得到所有当自己处于离线时发送到主题的消息
   &emsp;当持久订阅状态下，不能恢复或重新派送一个未签收的消息。
   &emsp;持久订阅才能恢复或重新派送一个未签收的消息。

   (4)	非持久和持久化订阅如何选择
   &emsp;当所有的消息必须被接收，则用持久化订阅。当消息丢失能够被容忍，则用非持久订阅。
### 五、ActiveMQ的broker
> (1)	 broker是什么
> &emsp;相当于一个ActiveMQ服务器实例。说白了，Broker其实就是实现了用代码的形式启动ActiveMQ将MQ嵌入到Java代码中，以便随时用随时启动，再用的时候再去启动这样能节省了资源，也保证了可用性。这种方式，我们实际开发中很少采用，因为他缺少太多了东西，如：日志，数据存储等等。
> (2) 启动broker时指定配置文件
> &emsp;启动broker时指定配置文件，可以帮助我们在一台服务器上启动多个broker。实际工作中一般一台服务器只启动一个broker。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200929215244488.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> (3)	嵌入式的broker启动
> &emsp;用ActiveMQ Broker作为独立的消息服务器来构建Java应用。
> ActiveMQ也支持在vm中通信基于嵌入的broker，能够无缝的集成其他java应用。
```java
//pom.xml添加一个依赖
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.10.1</version>
</dependency>
```
```java
//嵌入式broke的启动类
import org.apache.activemq.broker.BrokerService;

public class EmbedBroker {

    public static void main(String[] args) throws Exception {
        //ActiveMQ也支持在vm中通信基于嵌入的broker
        BrokerService brokerService = new BrokerService();
        brokerService.setPopulateJMSXUserID(true);
        brokerService.addConnector("tcp://127.0.0.1:61616");
        brokerService.start();
   }
}
```
### 六、Spring整合ActiveMQ
#### 1.  添加依赖
```xml
<dependencies>
   <!-- activemq核心依赖包  -->
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-all</artifactId>
            <version>5.15.9</version>
        </dependency>
        <dependency>
            <groupId>org.apache.xbean</groupId>
            <artifactId>xbean-spring</artifactId>
            <version>3.16</version>
        </dependency>
        <!--  嵌入式activemq的broker所需要的依赖包   -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.5</version>
        </dependency>
        <!-- activemq连接池 -->
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-pool</artifactId>
            <version>5.15.9</version>
        </dependency>
        <!-- spring支持jms的包 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
            <version>4.3.23.RELEASE</version>
        </dependency>
        <!-- Spring核心依赖 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.3.23.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>4.3.23.RELEASE</version>
        </dependency>
</dependencies>

```
#### 2. spring.xml文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	        http://www.springframework.org/schema/beans/spring-beans.xsd
	        http://www.springframework.org/schema/context
	        http://www.springframework.org/schema/context/spring-context.xsd
	        http://www.springframework.org/schema/aop
	        http://www.springframework.org/schema/aop/spring-aop.xsd
	        http://www.springframework.org/schema/tx
	        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 包扫描、只要是标注了@Service、@Repository、@Component、@Controller -->
    <context:component-scan base-package="com.kun">
    </context:component-scan>

    <!--配置连接池-->
    <bean id="connectionFactory" class="org.apache.activemq.pool.PooledConnectionFactory"
          destroy-method="stop">
        <!--真正生产Connection的ConnectionFactory，由对应的JMS服务商提供-->
        <property name="connectionFactory">
            <bean class="org.apache.activemq.spring.ActiveMQConnectionFactory">
                <property name="brokerURL" value="tcp://***:61616"/>
            </bean>
        </property>
        <property name="maxConnections" value="100"/>
    </bean>

    <!--队列的目的地，点对点的Queue-->
    <bean id="activeMQQueue" class="org.apache.activemq.command.ActiveMQQueue">
        <!--通过构造注入Queue的名-->
        <constructor-arg index="0" value="spring-active-queue"></constructor-arg>
    </bean>

    <!--主题的目的地，发布订阅的主题Topic-->
    <bean class="org.apache.activemq.command.ActiveMQTopic" id="activeMQTopic">
        <constructor-arg index="0" value="spring-active-topic"/>
    </bean>

    <!--Spring 提供的JMS工具类，它可以进行消息发送，接收等-->
    <bean class="org.springframework.jms.core.JmsTemplate" id="jmsTemplate">
        <!--传入连接工厂-->
        <property name="connectionFactory" ref="connectionFactory"/>
        <!--传入目的地，当前是队列-->
        <property name="defaultDestination" ref="activeMQQueue"/>
        <!-- <property name="defaultDestination" ref="activeMQTopic"/>-->
        <!--消息自动转换器-->
        <property name="messageConverter">
            <bean class="org.springframework.jms.support.converter.SimpleMessageConverter"></bean>
        </property>
    </bean>
</beans>

```
#### 3. 队列编码
```java
//生产者
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
```
```java
//消费者
package com.kun;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
```
#### 4. 主题编码
```java
//队列中的编码不用修改就可以用。
```
#### 5.配置消费者的监听类
> 不启动消费者，生产者生产消息，消费者自动接收消息
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	        http://www.springframework.org/schema/beans/spring-beans.xsd
	        http://www.springframework.org/schema/context
	        http://www.springframework.org/schema/context/spring-context.xsd
	        http://www.springframework.org/schema/aop
	        http://www.springframework.org/schema/aop/spring-aop.xsd
	        http://www.springframework.org/schema/tx
	        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 包扫描、只要是标注了@Service、@Repository、@Component、@Controller -->
    <context:component-scan base-package="com.kun">
    </context:component-scan>

    <!--配置连接池-->
    <bean id="connectionFactory" class="org.apache.activemq.pool.PooledConnectionFactory"
          destroy-method="stop">
        <!--真正生产Connection的ConnectionFactory，由对应的JMS服务商提供-->
        <property name="connectionFactory">
            <bean class="org.apache.activemq.spring.ActiveMQConnectionFactory">
                <property name="brokerURL" value="tcp://***:61616"/>
            </bean>
        </property>
        <property name="maxConnections" value="100"/>
    </bean>

    <!--队列的目的地，点对点的Queue-->
    <bean id="activeMQQueue" class="org.apache.activemq.command.ActiveMQQueue">
        <!--通过构造注入Queue的名-->
        <constructor-arg index="0" value="spring-active-queue"></constructor-arg>
    </bean>

    <!--主题的目的地，发布订阅的主题Topic-->
    <bean class="org.apache.activemq.command.ActiveMQTopic" id="activeMQTopic">
        <constructor-arg index="0" value="spring-active-topic"/>
    </bean>

    <!--Spring 提供的JMS工具类，它可以进行消息发送，接收等-->
    <bean class="org.springframework.jms.core.JmsTemplate" id="jmsTemplate">
        <!--传入连接工厂-->
        <property name="connectionFactory" ref="connectionFactory"/>
        <!--传入目的地-->
        <property name="defaultDestination" ref="activeMQTopic"/>
        <!--消息自动转换器-->
        <property name="messageConverter">
            <bean class="org.springframework.jms.support.converter.SimpleMessageConverter"></bean>
        </property>
    </bean>

    <!--配置消费者监听程序-->
    <bean class="org.springframework.jms.listener.DefaultMessageListenerContainer" id="jmsContainer">
        <property name="connectionFactory" ref="connectionFactory"/>
        <property name="destination" ref="activeMQTopic"/>
        <property name="messageListener" ref="myMessageListener"/>
    </bean>

</beans>
```
```java
package com.kun;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class MyMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        if(null != message && message instanceof TextMessage){
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println(textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
```
### 	七、Springboot整合ActiveMQ
#### 1. 队列
> - pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.kun</groupId>
    <artifactId>boot_mq</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>boot_mq</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-activemq</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
<!--            <exclusions>-->
<!--                <exclusion>-->
<!--                    <groupId>org.junit.vintage</groupId>-->
<!--                    <artifactId>junit-vintage-engine</artifactId>-->
<!--                </exclusion>-->
<!--            </exclusions>-->
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
> - application.yml
```yml
spring:
  activemq:
    # activemq的broker的url
    broker-url: tcp://121.199.70.188:61616
    # 连接activemq的broker所需的账号和密码
    user: admin
    password: admin
  jms:
    # 目的地是queue还是topic，false（默认) = queue , true = topic
    pub-sub-domain: false
# 自定义队列名称，只是个常量
myqueue: boot-activemq-queue
```
> - ConfigBean.java
```java
package com.kun.boot_mq.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component
@EnableJms //开启Jms的适配注解
public class ConfigBean {
    @Value("${myqueue}")
    private String myQueue;

    @Bean //bean id="" class = ""
    public Queue queue(){
        return new ActiveMQQueue(myQueue);
    }
}
```
> - Queue_Produce.java ==生产者==
```java
package com.kun.boot_mq;

import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Queue;
import java.util.UUID;

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

    }
}
```
> - Queue_Consumer.java ==消费者==
```java
package com.kun.boot_mq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

@Component
public class Queue_Consumer {
    // 注册一个监听器。destination指定监听的主题。
    @JmsListener(destination = "${myqueue}")
    public void receive(TextMessage textMessage) throws  Exception{
        System.out.println(" ***  消费者收到消息  ***"+textMessage.getText());
    }

}

```
> - TestActiveMQ.java
```java
package com.kun.boot_mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

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
```
> - BootMqApplication.java
```java
package com.kun.boot_mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //使@Scheduled生效
public class BootMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootMqApplication.class, args);
    }

}
```
#### 2. 主题
> - pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.kun</groupId>
    <artifactId>boot_mq</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>boot_mq</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-activemq</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
<!--            <exclusions>-->
<!--                <exclusion>-->
<!--                    <groupId>org.junit.vintage</groupId>-->
<!--                    <artifactId>junit-vintage-engine</artifactId>-->
<!--                </exclusion>-->
<!--            </exclusions>-->
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
> - application.yml
```yml
spring:
  activemq:
    # activemq的broker的url
    broker-url: tcp://121.199.70.188:61616
    # 连接activemq的broker所需的账号和密码
    user: admin
    password: admin
  jms:
    # 目的地是queue还是topic，false（默认) = queue , true = topic
    pub-sub-domain: true
# 自定义队列名称，只是个常量
mytopic: boot-activemq-topic
```
> - ConfigBean.java
```java
package com.kun.boot_mq.config;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.stereotype.Component;
import javax.jms.Topic;

@Component
@EnableJms
public class ConfigBean {

    @Value("${mytopic}")
    private String  topicName ;

    @Bean
    public Topic topic() {
        return new ActiveMQTopic(topicName);
    }
}

```
> - Topic_Produce.java ==生产者==
```java
package com.kun.boot_mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.jms.Topic;
import java.util.UUID;

@Component
public class Topic_Produce {

    @Autowired
    private JmsMessagingTemplate  jmsMessagingTemplate ;

    @Autowired
    private Topic  topic ;

    @Scheduled(fixedDelay = 3000)
    public void produceTopic(){
        jmsMessagingTemplate.convertAndSend(topic,"主题消息"+ UUID.randomUUID().toString().substring(0,6));
    }
}

```
> - Topic_Consummer.java ==消费者==
```java
package com.kun.boot_mq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import javax.jms.TextMessage;

@Component
public class Topic_Consummer {

    @JmsListener(destination = "${mytopic}")
    public void receive(TextMessage textMessage) throws  Exception{
        System.out.println("消费者受到订阅的主题："+textMessage.getText());
    }
}
```
> - BootMqApplication.java
```java
package com.kun.boot_mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //使@Scheduled生效
public class BootMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootMqApplication.class, args);
    }

}
```
### 	八、ActiveMQ的传输协议
> ==简介==：
> &emsp;&emsp;ActiveMQ支持的client-broker通讯协议有：TCP、NIO、UDP、SSL、Http(s)、VM。其中配置Transport Connector的文件在ActiveMQ安装目录的conf/activemq.xml中的<transportConnectors>标签之内。
> &emsp;&emsp;activemq传输协议的官方文档：[http://activemq.apache.org/configuring-version-5-transports.html](http://activemq.apache.org/configuring-version-5-transports.html)

> activemq.xml
```xml
<!--
	在配置信息中，URI描述信息的头部都是采用协议名称：例如
	描述amqp协议的监听端口时，采用的URI描述格式为“amqp://······”；
	描述Stomp协议的监听端口时，采用URI描述格式为“stomp://······”；
	唯独在进行openwire协议描述时，URI头却采用的“tcp://······”。这是因为ActiveMQ中默认的消息协议就是openwire
-->
 <transportConnectors>
            <!-- DOS protection, limit concurrent connections to 1000 and frame size to 100MB -->
            <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="amqp" uri="amqp://0.0.0.0:5672?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="stomp" uri="stomp://0.0.0.0:61613?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="ws" uri="ws://0.0.0.0:61614?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
 </transportConnectors>
```
#### 1. 支持的传输协议
> 个人说明：除了tcp和nio协议，其他的了解就行。各种协议有各自擅长该协议的中间件，工作中一般不会使用activemq去实现这些协议。如： mqtt是物联网专用协议，采用的中间件一般是mosquito。ws是websocket的协议，是和前端对接常用的，一般在java代码中内嵌一个基站（中间件）。stomp好像是邮箱使用的协议的，各大邮箱公司都有基站（中间件）。
> 注意：协议不同，我们的代码都会不同。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201006201446914.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

#### 2. TCP协议
>(1)	Transmission Control Protocol(TCP)是默认的。TCP的Client监听端口61616
>(2)	在网络传输数据前，必须要先序列化数据，消息是通过一个叫wire protocol的来序列化成字节流。
>(3)	TCP连接的URI形式如：tcp://HostName:port?key=value&key=value，后面的参数是可选的。
>(4)	TCP传输的的优点：
>&emsp;&emsp;TCP协议传输可靠性高，稳定性强
>&emsp;&emsp;高效率：字节流方式传递，效率很高
>&emsp;&emsp;有效性、可用性：应用广泛，支持任何平台
>(5)	关于Transport协议的可选配置参数可以参考官网[http://activemq.apache.org/tcp-transport-reference](http://activemq.apache.org/tcp-transport-reference)
#### 3. NIO协议
> (1)	New I/O API Protocol(NIO)
> (2)	NIO协议和TCP协议类似，但NIO更侧重于底层的访问操作。它允许开发人员对同一资源可有更多的client调用和服务器端有更多的负载。
> (3)	适合使用NIO协议的场景：
> &emsp;&emsp;可能有大量的Client去连接到Broker上，一般情况下，大量的Client去连接Broker是被操作系统的线程所限制的。因此，NIO的实现比TCP需要更少的线程去运行，所以建议使用NIO协议。
> &emsp;&emsp;![在这里插入图片描述](https://img-blog.csdnimg.cn/20201006202119451.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 可能对于Broker有一个很迟钝的网络传输，NIO比TCP提供更好的性能。
> (4)	NIO连接的URI形式：nio://hostname:port?key=value&key=value
> (5)	关于Transport协议的可选配置参数可以参考官网[http://activemq.apache.org/configuring-version-5-transports.html](http://activemq.apache.org/configuring-version-5-transports.html)
#### 4. AMQP协议
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201006202556183.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
#### 5. STOMP协议
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100620272189.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201006202726560.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)


#### 6. MQTT协议
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201006202644894.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
#### 7. NIO协议案例
> - ActiveMQ这些协议传输的底层默认都是使用BIO网络的IO模型。只有当我们指定使用nio才使用NIO的IO模型。

> (1)	修改配置文件activemq.xml
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201006203240672.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> ①　修改配置文件activemq.xml在 <transportConnectors>节点下添加如下内容：
> &emsp;`<transportConnector name="nio" uri="nio://0.0.0.0:61618?trace=true" />`
> ②　修改完成后重启activemq:  
> &emsp;`service activemq  restart`
> ③　查看管理后台，可以看到页面多了nio
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201006203553223.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> (2)	编写代码
```java
//生产者
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Jms_TX_Producer {

    private static final String ACTIVEMQ_URL = "nio://***:61618";

    private static final String ACTIVEMQ_QUEUE_NAME = "nio-test";

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageProducer producer = session.createProducer(queue);
        try {
            for (int i = 0; i < 3; i++) {
                TextMessage textMessage = session.createTextMessage("tx msg--" + i);
                producer.send(textMessage);
            }
            System.out.println("消息发送完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //8.关闭资源
            producer.close();
            session.close();
            connection.close();
        }
    }
}
```
```java
//消费者
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.IOException;

public class Jms_TX_Consumer {
    private static final String ACTIVEMQ_URL = "nio://***:61618";
    private static final String ACTIVEMQ_QUEUE_NAME = "nio-test";

    public static void main(String[] args) throws JMSException, IOException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(new MessageListener() {

            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    try {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("***消费者接收到的消息:   " + textMessage.getText());
                    } catch (Exception e) {
                        System.out.println("出现异常，消费失败，放弃消费");
                    }
                }
            }
        });
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
```
#### 8. NIO协议案例增强
> (1)	==目的==
> - 上面是Openwire协议传输底层使用NIO网络IO模型。 如何让其他协议传输底层也使用NIO网络IO模型呢？
>   ![请添加图片描述](https://img-blog.csdnimg.cn/20201006212151425.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
>   ![请添加图片描述](https://img-blog.csdnimg.cn/20201006212151406.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> (2)	修改配置文件activemq.xml
```xml
<transportConnectors>
	<transportConnector name="openwire" uri="tcp://0.0.0.0:61626?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
	<transportConnector name="amqp" uri="amqp://0.0.0.0:5682?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
	<transportConnector name="stomp" uri="stomp://0.0.0.0:61623?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
	<transportConnector name="mqtt" uri="mqtt://0.0.0.0:1893?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
	<transportConnector name="ws" uri="ws://0.0.0.0:61624?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
	<transportConnector name="nio" uri="nio://0.0.0.0:61618?trace=true" />
	<transportConnector name="auto+nio" uri="auto+nio://0.0.0.0:61608?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600&amp;org.apache.activemq.transport.nio.SelectorManager.corePoolSize=20&amp;org.apache.activemq.transport.nio.Se1ectorManager.maximumPoo1Size=50"/>
</transportConnectors>
```
> 官方文档：[http://activemq.apache.org/auto](http://activemq.apache.org/auto)
> auto	: 针对所有的协议，他会识别我们是什么协议（openwire、stomp、amqp、mqtt会被识别）
> nio		：使用NIO网络IO模型
> `修改配置文件后重启activemq。`

> (3)	代码
```java
//使用nio模型的tcp协议生产者。其他代码和之前一样
public class Jms_TX_Producer {
    private static final String ACTIVEMQ_URL = "tcp://118.24.20.3:61608";
    private static final String ACTIVEMQ_QUEUE_NAME = "auto-nio";

    public static void main(String[] args) throws JMSException {
         ......
    }
}
//使用nio模型的tcp协议消费者。其他代码和之前一样
public class Jms_TX_Consumer {
    private static final String ACTIVEMQ_URL = "tcp://118.24.20.3:61608";
    private static final String ACTIVEMQ_QUEUE_NAME = "auto-nio";

    public static void main(String[] args) throws JMSException, IOException {
       ......
    }
}
//使用nio模型的nio协议生产者。其他代码和之前一样
public class Jms_TX_Producer {
    private static final String ACTIVEMQ_URL = "nio://118.24.20.3:61608";
    private static final String ACTIVEMQ_QUEUE_NAME = "auto-nio";

    public static void main(String[] args) throws JMSException {
       ......
    }
}
//使用nio模型的nio协议消费者。其他代码和之前一样
public class Jms_TX_Consumer {
    private static final String ACTIVEMQ_URL = "nio://118.24.20.3:61608";
    private static final String ACTIVEMQ_QUEUE_NAME = "auto-nio";

    public static void main(String[] args) throws JMSException, IOException {
        ......
    }
}
```

### 	九、ActiveMQ的消息存储和持久化
#### 1. 介绍
> (1)	此处持久化和之前的持久化的区别
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009094531256.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> MQ高可用：事务、可持久、签收，是属于MQ自身特性，自带的。这里的持久化是外力，是外部插件。之前讲的持久化是MQ的外在表现，现在讲的的持久是是底层实现。
> </br>
> </br>
> (2)	是什么：
> 官网文档：[http://activemq.apache.org/persistence](http://activemq.apache.org/persistence)
> &emsp;&emsp;==持久化是什么==？一句话就是：ActiveMQ宕机了，消息不会丢失的机制。
> &emsp;&emsp;==说明==：为了避免意外宕机以后丢失信息，需要做到重启后可以恢复消息队列，消息系统一半都会采用持久化机制。ActiveMQ的消息持久化机制有JDBC，AMQ，KahaDB和LevelDB，无论使用哪种持久化方式，消息的存储逻辑都是一致的。就是在发送者将消息发送出去后，消息中心首先将消息存储到本地数据文件、内存数据库或者远程数据库等。再试图将消息发给接收者，成功则将消息从存储中删除，失败则继续尝试尝试发送。消息中心启动以后，要先检查指定的存储位置是否有未成功发送的消息，如果有，则会先把存储位置中的消息发出去。


#### 2. 有哪些？
> (1)	AMQ Message Store
> 基于文件的存储机制，是以前的默认机制，现在不再使用。
> AMQ是一种文件存储形式，它具有写入速度快和容易恢复的特点。消息存储再一个个文件中文件的默认大小为32M，当一个文件中的消息已经全部被消费，那么这个文件将被标识为可删除，在下一个清除阶段，这个文件被删除。AMQ适用于ActiveMQ5.3之前的版本

> (2)	kahaDB
> 现在默认的。下面我们再详细介绍。

> (3)	JDBC消息存储
> 下面我们再详细介绍。

> (4)	LevelDB消息存储
> 过于新兴的技术，现在有些不确定。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100909583717.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> (5)	JDBC Message Store with ActiveMQ Journal
> 下面我们再详细介绍。

#### 3. kahaDB消息存储
> (1)	介绍
> 基于日志文件，从ActiveMQ5.4（含）开始默认的持久化插件。
> 官网文档：[http://activemq.aache.org/kahadb](http://activemq.aache.org/kahadb)
> 官网上还有一些其他配置参数。
> 配置文件activemq.xml中，如下：
```xml
   <persistenceAdapter>
         <kahaDB directory="${activemq.data}/kahadb"/>
   </persistenceAdapter>
```
> 日志文件的存储目录在：%activemq安装目录%/data/kahadb

> (2)	说明
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009100502690.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> (3)	KahaDB的存储原理
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009100747537.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009100753947.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
#### 4. JDBC消息存储
##### 4.1 设置步骤
> (1) 原理图
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100910205648.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> (2)	添加mysql数据库的驱动包到lib文件夹
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009102858495.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> (3)	 jdbcPersistenceAdapter配置
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009103003572.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 修改配置文件activemq.xml。将之前的替换为jdbc的配置。如下：
```xml
<!--  
<persistenceAdapter>
            <kahaDB directory="${activemq.data}/kahadb"/>
      </persistenceAdapter>
-->
<persistenceAdapter>  
      <jdbcPersistenceAdapter dataSource="#mysql-ds" createTableOnStartup="true"/> 
</persistenceAdapter>
```
> (4)	数据库连接池配置
> 需要我们准备一个mysql数据库，并创建一个名为activemq的数据库。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009103317175.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 在</broker>标签和<import>标签之间插入数据库连接池配置![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009103350165.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 具体操作如下：
```xml
    </broker>

    <bean id="mysql-ds" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://mysql数据库URL/activemq?relaxAutoCommit=true"/>
        <property name="username" value="mysql数据库用户名"/>
        <property name="password" value="mysql数据库密码"/>
        <property name="poolPreparedStatements" value="true"/>
    </bean>

    <import resource="jetty.xml"/>
```
> 之后需要建一个数据库，名为activemq。新建的数据库要采用latin1 或者ASCII编码。[https://blog.csdn.net/JeremyJiaming/article/details/88734762](https://blog.csdn.net/JeremyJiaming/article/details/88734762)
> 默认是的dbcp数据库连接池，如果要换成其他数据库连接池，需要将该连接池jar包，也放到lib目录下。

> (5)	建库SQL和创表说明
> `先新建一个数据库activemq`
> 重启activemq。会自动生成如下3张表。如果没有自动生成，需要我们手动执行SQL。个人建议要自动生成，我在操作过程中查看日志文件，发现了不少问题，最终解决了这些问题后，是能够自动生成的。
> ==ACTIVEMQ_MSGS数据表：==
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009105517239.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> ==ACTIVEMQ_ACKS数据表：==
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009105551742.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> ==ACTIVEMQ_LOCK数据表：==
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009105627347.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
##### 4.2 queue验证和数据表变化
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100911015136.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> queue模式，非持久化不会将消息持久化到数据库。
> queue模式，持久化会将消息持久化数据库。

> 我们使用queue模式持久化，发布3条消息后，发现ACTIVEMQ_MSGS数据表多了3条数据。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009110231316.png#pic_center)
> 启动消费者，消费了所有的消息后，发现数据表的数据消失了。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009110258680.png#pic_center)
> queue模式非持久化，不会持久化消息到数据表。

##### 4.3 topic验证和说明
> 我们先启动一下持久化topic的消费者。看到ACTIVEMQ_ACKS数据表多了一条消息。
```java
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

// 持久化topic 的消息消费者
public class JmsConsummer_persistence {
    private static final String ACTIVEMQ_URL = "tcp://***:61626";
    public static final String TOPIC_NAME = "jdbc-02";

    public static void main(String[] args) throws Exception{
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.setClientID("marrry");
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic,"remark...");
        connection.start();
        Message message = topicSubscriber.receive();
        while (null != message){
            TextMessage textMessage = (TextMessage)message;
            System.out.println(" 收到的持久化 topic ："+textMessage.getText());
            message = topicSubscriber.receive();
        }
        session.close();
        connection.close();
    }
}
```
> ACTIVEMQ_ACKS数据表，多了一个消费者的身份信息。一条记录代表：一个持久化topic消费者。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009110645825.png#pic_center)
> 我们启动持久化生产者发布3个数据，ACTIVEMQ_MSGS数据表新增3条数据，消费者消费所有的数据后，ACTIVEMQ_MSGS数据表的数据并没有消失。持久化topic的消息不管是否被消费，是否有消费者，产生的数据永远都存在，且只存储一条。这个是要注意的，持久化的topic大量数据后可能导致性能下降。这里就像公总号一样，消费者消费完后，消息还会保留。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009110731370.png#pic_center)
##### 4.4 总结
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009110820656.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009110824110.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009110827366.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
#### 5. JDBC Message Store with ActiveMQ Journal
> (1)	说明
> &emsp;&emsp;这种方式克服了JDBC Store的不足，JDBC每次消息过来，都需要去写库读库。ActiveMQ Journal，使用高速缓存写入技术，大大提高了性能。当消费者的速度能够及时跟上生产者消息的生产速度时，journal文件能够大大减少需要写入到DB中的消息。
> &emsp;&emsp;举个例子：生产者生产了1000条消息，这1000条消息会保存到journal文件，如果消费者的消费速度很快的情况下，在journal文件还没有同步到DB之前，消费者已经消费了90%的以上消息，那么这个时候只需要同步剩余的10%的消息到DB。如果消费者的速度很慢，这个时候journal文件可以使消息以批量方式写到DB。
> &emsp;&emsp;为了高性能，这种方式使用日志文件存储+数据库存储。先将消息持久到日志文件，等待一段时间再将未消费的消息持久到数据库。该方式要比JDBC性能要高。

> (2)	配置
> 下面是基于上面JDBC配置，再做一点修改：
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009111035549.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009111039314.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
#### 6. 总结
> ①　jdbc效率低，kahaDB效率高，jdbc+Journal效率较高。
> ②　持久化消息主要指的是：MQ所在服务器宕机了消息不会丢试的机> 制。	
> ③　持久化机制演变的过程：
> &emsp;&emsp;从最初的AMQ Message Store方案到ActiveMQ V4版本退出的High Performance Journal（高性能事务支持）附件，并且同步推出了关于关系型数据库的存储方案。ActiveMQ5.3版本又推出了对KahaDB的支持（5.4版本后被作为默认的持久化方案），后来ActiveMQ 5.8版本开始支持LevelDB，到现在5.9提供了标准的Zookeeper+LevelDB集群化方案。
> ④　ActiveMQ消息持久化机制有：

|                          |                                                              |
| ------------------------ | ------------------------------------------------------------ |
| AMQ                      | 基于日志文件                                                 |
| KahaDB                   | 基于日志文件，从ActiveMQ5.4开始默认使用                      |
| JDBC                     | 基于第三方数据库                                             |
| Replicated LevelDB Store | 从5.9开始提供了LevelDB和Zookeeper的数据复制方法，用于Master-Slave方式的首选数据复制方案。 |
### 十、ActiveMQ多节点集群
> 1. 面试题？
>   引入消息队列之后该如何保证其高可用性

> 2. 是什么？
>   基于 Zookeeper和 LevelDB搭建 Activemq集群。集群仅提供主备方式的高可用集群功能,避免单点故障。

> 3. zookeeper + replicated-leveldb-store的主从集群
>   (1) 三种集群方式对比：[http://activemq.apache.org/masterslave.html](http://activemq.apache.org/masterslave.html)
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100911560873.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
>   &emsp;&emsp; 基于sharedFileSystem共享文件系统（KahaDB）
>   &emsp;&emsp;基于JDBC
>   &emsp;&emsp;基于可复制的LevelDB
>   （2）`zookeeper + replicated-leveldb-store的主从集群`==(重点)==
>   &emsp;&emsp;&emsp;下面重点介绍

> 4. `zookeeper + replicated-leveldb-store的主从集群`
>   4.1 是什么
>   [http://activemq.apache.org/replicated-leveldb-store.html](http://activemq.apache.org/replicated-leveldb-store.html)
>   4.2 官网集群管理图
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009134258637.png#pic_center)
>   &emsp;&emsp;它使用Apache ZooKeeper来协调集群中哪个节点成为主控节点。当选的主代理节点启动并接受客户端连接。其他节点进入从属模式，连接主节点，并同步他们的持久状态/w它。从属节点不接受客户端连接。所有的持久化操作都会复制到连接的从机上。如果主节点死亡，有最新更新的从节点会被提升为主节点。然后，失败的节点可以重新上线，它将进入从机模式。<br/>
>   &emsp;&emsp;所有需要同步到磁盘上的消息传递操作都会等待更新复制到法定数量的节点上才会完成。所以，如果你配置的存储是 replicas="3"，那么法定人数就是（3/2+1）=2。主站会将更新存储在本地，并等待另外1个从站存储更新后再报告成功。另一种思路是，store会对法定人数的复制节点进行同步复制，对任何其他节点进行异步复制复制。<br/>
>   &emsp;&emsp;当选出一个新的主控时，你还需要至少有一个法定人数的节点在线，才能够找到一个有最新更新的节点。拥有最新更新的节点将成为新的主站。因此，建议你至少使用3个复制节点来运行，这样你就可以在不遭受服务中断的情况下干掉一个节点。<br/>
>   ==说明：==
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009134958801.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 4.3 部署规划和步骤
> （1）环境和版本
> CentOS release 6.8(Final)、JDK1.8、zookeeper-3.4.9、apache-activemq-5.15.9<br/>
> （2）关闭防火墙并保证win可以ping通ActiveMQ服务器<br/>
> （3）要求具备ZK集群并可以成功启动<br/>
> （4）集群部署规划列表
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009140817425.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)<br/>
> （5）创建3台集群目录
> mkdir /data/mq_cluster/
> cd /data/mq_cluster/
> cp -r /data/activemq/apache-activemq-5.16.0 mq_node01
> cp -r mq_node01 mq_node02
> cp -r mq_node01 mq_node03
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009142019627.png#pic_center)<br/>
> （6）修改管理控制台端口
> mq_node01全部默认不动
> mq_node02修改：
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100914221758.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> mq_node03修改：同上。<br/>
> （7）hostname名字映射
> 题外话：在windows下在C:\Windows\System32\drivers\etc下的hosts文件中配置ip和域名的映射。
> linux下：
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009143112137.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> <br/>
> （8）ActiveMQ集群配置
> 01/02/03节点路径
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009143714118.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 3个节点的BrokerName要求全部一致
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009143838389.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 3个节点的持久化配置，在activemq.xml中修改
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009144734455.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

```xml
<persistenceAdapter>
    <replicatedLevelDB
      directory="activemq-data"
      replicas="3"
      bind="tcp://0.0.0.0:0"
      zkAddress="zoo1.example.org:2181,zoo2.example.org:2181,zoo3.example.org:2181"
      zkPassword="password"
      zkPath="/activemq/leveldb-stores"
      hostname="broker1.example.org"
      />
  </persistenceAdapter>
```
> （9）修改各节点的消息端口
> mq_node01全部默认不动 61616
> mq_node02修改：61617
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009145047858.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> mq_node03修改：61618
> </br>
> （10）按顺序启动3个ActiveMQ节点，到这步前提是zk集群已经成功启动运行
> zk启动批处理：
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009145348943.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> mq启动批处理:
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009145308498.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)</br>
> （11）zk集群的节点状态说明
> 3台zk集群连接任意一台
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009145800544.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 查看master
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009145705407.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 4. Replicated LevelDB集群故障迁移和验证
>   (1) 集群可用性测试
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009150127762.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
>   （2）代码修改（由单机到集群）
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009150251704.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
>   (3) 干掉一台ActiveMQ节点，它会自动切换到另外一个活着的。
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009150455806.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
### 十一、高级特性
#### 1. 异步投递
> (1) 是什么：
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009150828663.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 自我理解：此处的异步是指生产者和broker之间发送消息的异步。不是指生产者和消费者之间异步。
> 官网介绍：[http://activemq.apache.org/async-sends](http://activemq.apache.org/async-sends)
> 总结：
> ①　异步发送可以让生产者发的更快。
> ②　如果异步投递不需要保证消息是否发送成功，发送者的效率会有所提高。如果异步投递还需要保证消息是否成功发送，并采用了回调的方式，发送者的效率提高不多，这种就有些鸡肋。

> (2) 代码实现
> 官网上3中代码实现：
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009205037944.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
```java
//代码演示。
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Jms_TX_Producer {

    // 方式1。3种方式任选一种
    private static final String ACTIVEMQ_URL = "tcp://118.24.20.3:61626?jms.useAsyncSend=true";
    private static final String ACTIVEMQ_QUEUE_NAME = "Async";

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 方式2
        activeMQConnectionFactory.setUseAsyncSend(true);
        Connection connection = activeMQConnectionFactory.createConnection();
        // 方式3
        ((ActiveMQConnection)connection).setUseAsyncSend(true);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageProducer producer = session.createProducer(queue);
        try {
            for (int i = 0; i < 3; i++) {
                TextMessage textMessage = session.createTextMessage("tx msg--" + i);
                producer.send(textMessage);
            }
            System.out.println("消息发送完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
            session.close();
            connection.close();
        }
    }
}
```

> (3) 异步发送如何确认发送成功
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009205531569.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
```java
//下面演示异步发送的回调
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.AsyncCallback;

import javax.jms.*;
import java.util.UUID;

public class Jms_TX_Producer {

    private static final String ACTIVEMQ_URL = "tcp://118.24.20.3:61626";

    private static final String ACTIVEMQ_QUEUE_NAME = "Async";

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        activeMQConnectionFactory.setUseAsyncSend(true);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        ActiveMQMessageProducer activeMQMessageProducer = (ActiveMQMessageProducer)session.createProducer(queue);
        try {
            for (int i = 0; i < 3; i++) {
                TextMessage textMessage = session.createTextMessage("tx msg--" + i);
                textMessage.setJMSMessageID(UUID.randomUUID().toString()+"orderAtguigu");
                final String  msgId = textMessage.getJMSMessageID();
                activeMQMessageProducer.send(textMessage, new AsyncCallback() {
                    public void onSuccess() {
                        System.out.println("成功发送消息Id:"+msgId);
                    }

                    public void onException(JMSException e) {
                        System.out.println("失败发送消息Id:"+msgId);
                    }
                });
            }
            System.out.println("消息发送完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            activeMQMessageProducer.close();
            session.close();
            connection.close();
        }
    }
}
```
> 控制台观察发送消息的信息：
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009205814530.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201009205818346.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
#### 2. 延迟投递和定时投递
> (1)	介绍
> 官网文档：[http://activemq.apache.org/delay-and-schedule-message-delivery.html](http://activemq.apache.org/delay-and-schedule-message-delivery.html)
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010085852752.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)


![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010085858120.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 2) 修改配置文件并重启
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010085949976.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
```xml
<!-- 在activemq.xml添加如下灰色背景代码：-->
 </bean>

    <broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}"  schedulerSupport="true" >

        <destinationPolicy>
<!-- 之后重启activemq -->
```

> 3)	代码实现
```java
//java代码里面封装的辅助消息类型：ScheduleMessage
//生产者代码。
package com.activemq.demo;

import org.apache.activemq.*;
import javax.jms.*;
import java.util.UUID;

public class Jms_TX_Producer {

    private static final String ACTIVEMQ_URL = "tcp://118.24.20.3:61626";

    private static final String ACTIVEMQ_QUEUE_NAME = "Schedule01";

    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageProducer messageProducer = session.createProducer(queue);
        long delay =  10*1000;
        long period = 5*1000;
        int repeat = 3 ;
        try {
            for (int i = 0; i < 3; i++) {
                TextMessage textMessage = session.createTextMessage("tx msg--" + i);
                // 延迟的时间
                textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
                // 重复投递的时间间隔
                textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
                // 重复投递的次数
                textMessage.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);
                // 此处的意思：该条消息，等待10秒，之后每5秒发送一次，重复发送3次。
                messageProducer.send(textMessage);
            }
            System.out.println("消息发送完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            messageProducer.close();
            session.close();
            connection.close();
        }
    }
}
```
```java
//消费者代码
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.IOException;

public class Jms_TX_Consumer {

    private static final String ACTIVEMQ_URL = "tcp://118.24.20.3:61626";

    private static final String ACTIVEMQ_QUEUE_NAME = "Schedule01";

    public static void main(String[] args) throws JMSException, IOException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(new MessageListener() {

            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    try {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("***消费者接收到的消息:   " + textMessage.getText());
                        textMessage.acknowledge();
                    } catch (Exception e) {
                        System.out.println("出现异常，消费失败，放弃消费");
                    }
                }
            }
        });
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
```
#### 3.  消息消费的重试机制
>  (1)	是什么
>  官网文档：[http://activemq.apache.org/redelivery-policy](http://activemq.apache.org/redelivery-policy)
>  是什么： 消费者收到消息，之后出现异常了，没有告诉broker确认收到该消息，broker会尝试再将该消息发送给消费者。尝试n次，如果消费者还是没有确认收到该消息，那么该消息将被放到死信队列中，之后broker不会再将该消息发送给消费者。

> (2) 具体哪些情况会引发消息重发
> ①　Client用了transactions且在session中调用了rollback
> ②　Client用了transactions且在调用commit之前关闭或者没有commit
> ③　Client在CLIENT_ACKNOWLEDGE的传递模式下，session中调用了recover

> (3) 请说说消息重发时间间隔和重发次数
> 间隔：1
> 次数：6
> 每秒发6次

> (4)	有毒消息Poison ACK
> 一个消息被redelivedred超过默认的最大重发次数（默认6次）时，消费的回个MQ发一个“poison ack”表示这个消息有毒，告诉broker不要再发了。这个时候broker会把这个消息放到DLQ（死信队列）。

> (5)	属性说明
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010090552251.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> (6)	代码验证
```java
//生产者。发送3条数据。代码省略.....
/*消费者。开启事务，却没有commit。重启消费者，前6次都能收到消息，到第7次，不会再收到消息。代码：*/
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.IOException;

public class Jms_TX_Consumer {
    private static final String ACTIVEMQ_URL = "tcp://118.24.20.3:61626";
    private static final String ACTIVEMQ_QUEUE_NAME = "dead01";

    public static void main(String[] args) throws JMSException, IOException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        final Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("***消费者接收到的消息:   " + textMessage.getText());
                        //session.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        //关闭资源
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
```
> activemq管理后台。多了一个名为ActiveMQ.DLQ队列，里面多了3条消息。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010090707675.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> (7)	代码修改默认参数
```java
//消费者。修改重试次数为3(系统默认6次)。更多的设置请参考官网文档。
package com.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import javax.jms.*;
import java.io.IOException;

public class Jms_TX_Consumer {
    private static final String ACTIVEMQ_URL = "tcp://***:61626";
    private static final String ACTIVEMQ_QUEUE_NAME = "dead01";

    public static void main(String[] args) throws JMSException, IOException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 修改默认参数，设置消息消费重试3次
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);
        activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        final Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(ACTIVEMQ_QUEUE_NAME);
        MessageConsumer messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("***消费者接收到的消息:   " + textMessage.getText());
                        //session.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
```

> (8)	整合spring
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094010475.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> #### 4  死信队列
> (1)	是什么
> 官网文档： [http://activemq.apache.org/redelivery-policy](http://activemq.apache.org/redelivery-policy)
> 死信队列：异常消息规避处理的集合，主要处理失败的消息。
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020101009415996.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094202831.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094211269.png#pic_center)

> (2)	死信队列的配置（一般采用默认）
> 1.	sharedDeadLetterStrategy
>   不管是queue还是topic，失败的消息都放到这个队列中。下面修改activemq.xml的配置，可以达到修改队列的名字。
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094400209.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
>   </br>
>   .	individualDeadLetterStrategy
>   可以为queue和topic单独指定两个死信队列。还可以为某个话题，单独指定一个死信队列。
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094606264.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094610234.png#pic_center)
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094613875.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 3.	自动删除过期消息
>   过期消息是值生产者指定的过期时间，超过这个时间的消息。
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094656366.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)

> 4.	存放非持久消息到死信队列中
>   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010094721368.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
#### 5 消息不被重复消费，幂等性
> 如何保证消息不被重复消费呢？幕等性问题你谈谈
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010095409228.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201010095414458.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3h1YW54eHh4eHg=,size_16,color_FFFFFF,t_70#pic_center)
> 幂等性如何解决，根据messageid去查这个消息是否被消费了。
### 十二、扩展
> activemq的API文档
> [http://activemq.apache.org/maven/apidocs/index.html](http://activemq.apache.org/maven/apidocs/index.html)