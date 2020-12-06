package com.tusi.demo.config.aspect;

import com.tusi.demo.annotation.ReadOnly;
import com.tusi.demo.config.DynamicDataSourceHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
public class ReadOnlyAspect {

    @Pointcut("@annotation(com.tusi.demo.annotation.ReadOnly)")
//    @Pointcut("execution( * com.tusi.demo.service.*.*(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String methodStr = method.getName();
        log.info("Object is {}, execute method is {}", target, method);
        ReadOnly readOnly = method.getAnnotation(ReadOnly.class);

        // 如果方法上存在切换数据源的注解，则根据注解内容进行数据源切换
        if (readOnly != null && "slave".equals(readOnly.value())) {
            String dataSourceName = readOnly.value();
            String dataSourceNameNum = slaveLoadBalance();
            DynamicDataSourceHolder.putDataSource(dataSourceNameNum);
            log.info("current thread {}, add {} to ThreadLocal", Thread.currentThread().getName(), dataSourceName);
        } else {
            log.info("switch datasource fail, use default");
        }
    }

    // 将 ThreadLocal 中的数据源名清空
    @After("pointcut()")
    public void after(JoinPoint joinPoint){
        DynamicDataSourceHolder.removeDataSource();
    }

    // 从库负载均衡
    private static int i = 0;
    private String slaveLoadBalance() {
        if(i==Integer.MAX_VALUE) {
            i=0;
        }
        i++;
        if(i%2==0) {
            return "slave1";
        } else {
            return "slave2";
        }
    }
}
