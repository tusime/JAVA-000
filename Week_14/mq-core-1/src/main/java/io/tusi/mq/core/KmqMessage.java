package io.tusi.mq.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

/**
 * 消息：消息头，消息体
 */
@AllArgsConstructor
@Data
public class KmqMessage<T> {

    private HashMap<String,Object> headers;

    private T body;

}
