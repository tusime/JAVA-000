package io.tusi.mq.demo;

import io.tusi.mq.core.KmqBroker;
import io.tusi.mq.core.KmqConsumer;
import io.tusi.mq.core.KmqMessage;
import io.tusi.mq.core.KmqProducer;

import lombok.SneakyThrows;

public class KmqDemo {

    @SneakyThrows
    public static void main(String[] args) {

        String topic = "kk.test";
        // 基于内存不需要创建连接
        // 创建broker，topic
        KmqBroker broker = new KmqBroker();
        broker.createTopic(topic);

        // 创建消费者，订阅topic
        KmqConsumer consumer = broker.createConsumer();
        consumer.subscribe(topic);
        // flag 为 false 时退出程序
        final boolean[] flag = new boolean[1];
        flag[0] = true;
        // 线程异步消费
        new Thread(() -> {
            while (flag[0]) {
                KmqMessage<Order> message = consumer.poll(100);
                if(null != message) {
                    System.out.println(message.getBody());
                }
            }
            System.out.println("程序退出。");
        }).start();

        // 创建生产者，循环发送消息
        KmqProducer producer = broker.createProducer();
        for (int i = 0; i < 1000; i++) {
            Order order = new Order(1000L + i, System.currentTimeMillis(), "USD2CNY", 6.51d);
            producer.send(topic, new KmqMessage(null, order));
        }
        Thread.sleep(500);
        System.out.println("点击任何键，发送一条消息；点击q或e，退出程序。");
        // 死循环，构造手动发送消息
        while (true) {
            char c = (char) System.in.read();
            if(c > 20) {
                System.out.println(c);
                producer.send(topic, new KmqMessage(null, new Order(100000L + c, System.currentTimeMillis(), "USD2CNY", 6.52d)));
            }

            if( c == 'q' || c == 'e') {
                break;
            }
        }

        flag[0] = false;

    }
}
