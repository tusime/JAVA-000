package io.tusi.mq.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class CustomQueue<T> {
    private KmqMessage<T>[] queue;
    private String topic;
    private int capacity;
    // 生产位置
    private int pIndex;
    // 消费位置
    private int cIndex;
    // 可消费数
    private int count;

    private final ReentrantLock lock = new ReentrantLock();

    public CustomQueue(String topic, int capacity) {
        this.topic = topic;
        this.capacity = capacity;
        this.queue = new KmqMessage[capacity];
    }

    public boolean send(KmqMessage message) {
        try {
            this.lock.lock();
            if (count >= queue.length) {
                return false;
            }
            queue[pIndex] = message;
            pIndex++;
            count++;
            return false;
        } finally {
            lock.unlock();
        }
    }

    public KmqMessage poll() {
        try {
            this.lock.lock();
            if (count == 0) {
                return null;
            }
            // 数组模拟队列，先进先出也是从头部开始
            KmqMessage m = queue[cIndex];
            queue[cIndex] = null;
            cIndex++;
            count--;
            return m;
        } finally {
            lock.unlock();
        }
    }

    public KmqMessage poll(long timeout) {
        long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
        try {
            this.lock.lock();
            if (count == 0) {
                return null;
            }
            // 数组模拟队列，先进先出也是从头部开始
            KmqMessage m = queue[cIndex];
            queue[cIndex] = null;
            cIndex++;
            count--;
            return m;
        } finally {
            lock.unlock();
        }
    }
}
