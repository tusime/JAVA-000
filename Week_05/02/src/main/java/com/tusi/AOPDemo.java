package com.tusi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 使用 Java 动态代理，实现一个简单的 AOP
 */
public class AOPDemo {
    public static void main(String[] args) {
        Subject realSubject = new RealSubject();
        InvocationHandler handler = new DynamicProxy(realSubject);

        // 获取类，类实现接口
        ClassLoader classLoader = realSubject.getClass().getClassLoader();
        Class[] interfaces = realSubject.getClass().getInterfaces();
        // 指定类加载器、类实现接口、调用处理器生成动态代理类实例
        Subject subject = (Subject) Proxy.newProxyInstance(classLoader, interfaces, handler);

        System.out.println("代理类的类型：" + subject);
        System.out.println("代理后的实际类型：" + subject.getClass());
        System.out.println("代理后的实际类型是否是Subject子类：" + (subject instanceof Subject));
    }
}
