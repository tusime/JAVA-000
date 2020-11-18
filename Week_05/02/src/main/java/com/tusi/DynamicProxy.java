package com.tusi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicProxy implements InvocationHandler {

    private Object subject;
    // 代理对象赋值
    public DynamicProxy(Object subject) {
        this.subject = subject;
    }

    /**
     *
     * @param proxy 代理类
     * @param method 调用方法
     * @param args 方法参数
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        doBefore();
        Object res = method.invoke(subject, args);
        doAfter();
        return res;
    }

    public void doBefore() {
        System.out.println("DynamicProxy Before ...");
    }

    public void doAfter() {
        System.out.println("DynamicProxy After ...");
    }
}
