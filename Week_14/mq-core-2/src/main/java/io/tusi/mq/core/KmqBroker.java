package io.tusi.mq.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存多个 Kmq 队列
 */
public final class KmqBroker { // Broker+Connection

    public static final int CAPACITY = 10000;

    private final Map<String, CustomQueue> kmqMap = new ConcurrentHashMap<>(64);

    public void createTopic(String name){
        kmqMap.putIfAbsent(name, new CustomQueue(name, CAPACITY));
    }

    public CustomQueue findKmq(String topic) {
        return this.kmqMap.get(topic);
    }

    public KmqProducer createProducer() {
        return new KmqProducer(this);
    }

    public KmqConsumer createConsumer() {
        return new KmqConsumer(this);
    }

}
