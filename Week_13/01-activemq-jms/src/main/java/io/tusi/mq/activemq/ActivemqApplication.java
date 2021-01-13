package io.tusi.mq.activemq;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

/**
 * 参考
 * https://spring.io/guides/gs/messaging-jms/#initial
 * https://www.jb51.net/article/184486.htm
 */
@SpringBootApplication
@EnableJms
public class ActivemqApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ActivemqApplication.class, args);

        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("Sending...");
        //new Email("info@example.com", "Hello")
        jmsTemplate.convertAndSend(new ActiveMQQueue("mailbox"), "hello mailbox");
        jmsTemplate.convertAndSend(new ActiveMQTopic("noticebox"), "hello noticebox");
    }

}
