package io.tusi.kafkademo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "test1", groupId = "foo")
    public void kafkaListener(String message) {
        System.out.println("Received Message in group foo: " + message);
    }
}
