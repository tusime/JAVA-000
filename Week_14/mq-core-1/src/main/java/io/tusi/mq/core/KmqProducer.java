package io.tusi.mq.core;

public class KmqProducer {

    private KmqBroker broker;

    public KmqProducer(KmqBroker broker) {
        this.broker = broker;
    }

    public boolean send(String topic, KmqMessage message) {
        Kmq kmq = this.broker.findKmq(topic);
        if (null == kmq) {
            // 也可以自动 new 一个 kmq
            throw new RuntimeException("Topic[" + topic + "] doesn't exist.");
        }
        return kmq.send(message);
    }
}
