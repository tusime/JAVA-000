package io.tusi.mq.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

//    @JmsListener(destination = "mailbox")
//    public void receiveMessage(Email email) {
//        System.out.println("Received <" + email + ">");
//    }
    @JmsListener(destination = "mailbox", containerFactory = "queueJmsListenerContainerFactory")
    public void mailMessage(String msg) {
        System.out.println("Received <" + msg + ">");
    }
    @JmsListener(destination = "noticebox", containerFactory = "topicJmsListenerContainerFactory")
    public void noticeMessage(String msg) {
        System.out.println("Received <" + msg + ">");
    }
}
