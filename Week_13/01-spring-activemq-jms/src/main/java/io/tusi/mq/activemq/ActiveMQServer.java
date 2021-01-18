package io.tusi.mq.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ActiveMQServer {

    public static void main(String[] args) {

        // 定义Destination，topic 或 queue
        Destination destination = new ActiveMQTopic("test.topic");
        //Destination destination = new ActiveMQQueue("test.queue");
        testDestination(destination);
    }

    public static void testDestination(Destination destination) {
        try {
            // 连接工厂创建连接，建立连接，创建会话
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
            ActiveMQConnection conn = (ActiveMQConnection) factory.createConnection();
            conn.start();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 创建消费者
            MessageConsumer consumer = session.createConsumer( destination );
            final AtomicInteger count = new AtomicInteger(0);
            // 创建监听器，打印消费者接受内容
            MessageListener listener = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        // 测试背压，当客户端消费完后，mq才会再推送，客户端不会
                        // Thread.sleep();
                        // 打印所有的消息内容
                        System.out.println(count.incrementAndGet() + " => receive from " + destination.toString() + ": " + message);
                        // 前面所有未被确认的消息全部确认
                        // message.acknowledge();

                    } catch (Exception e) {
                        // 不要吞任何这里的异常，与服务器建立策略，异常后重发消息
                        e.printStackTrace();
                    }
                }
            };
            // 绑定消息监听器
            consumer.setMessageListener(listener);
            // 主动拉取消息，这里是activemq主动推送消息
            //consumer.receive()

            // 创建生产者，生产100个消息，发送给activemq
            MessageProducer producer = session.createProducer(destination);
            int index = 0;
            while (index++ < 100) {
                TextMessage message = session.createTextMessage(index + " message.");
                producer.send(message);
            }

            Thread.sleep(2000);
            session.close();
            conn.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
