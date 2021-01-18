package io.tusi.kafkademo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 参考
 * https://blog.csdn.net/qq_32641153/article/details/99132577
 * https://docs.spring.io/spring-kafka/docs/current/reference/html/#introduction
 * https://www.baeldung.com/spring-kafka
 */
@SpringBootApplication
public class KafkaDemoApplication implements CommandLineRunner {

    @Autowired
    private KafkaProducer kafkaProducer;



    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        kafkaProducer.sendMessageSync("test1", "hi");
    }


}
