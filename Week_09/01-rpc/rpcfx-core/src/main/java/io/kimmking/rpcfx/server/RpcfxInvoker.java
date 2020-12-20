package io.kimmking.rpcfx.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.exception.RpcfxException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * rpc 服务端
 */
public class RpcfxInvoker {

    private RpcfxResolver resolver;

    public RpcfxInvoker(RpcfxResolver resolver){
        this.resolver = resolver;
    }

    // request 获取 rpc 三要素，getServiceClass接口名，getMethod方法，getParams参数
    public RpcfxResponse invoke(RpcfxRequest request) {
        RpcfxResponse response = new RpcfxResponse();
        String serviceClass = request.getServiceClass();

        // 作业1：改成泛型和反射
        // 根据接口名获取bean
        //this.applicationContext.getBean(serviceClass);
        Object service = resolver.resolve(serviceClass);
        // 根据类型获取bean
//        try {
//            Class klass = Class.forName(serviceClass);
//            this.applicationContext.getBean(klass);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        try {
            // 反射获得方法
            Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
            System.out.println(method);
            // 调用方法 dubbo, fastjson 改进反射为字节码增强
            Object result = method.invoke(service, request.getParams());
            // 可以将两次json序列化能否合并成一个？可以，将@RestController去掉，统一使用fastjson序列化
            // 序列化 object->json 字符串，spring 再序列化（@RestController的作用）
            // RpcfxResponse 的 result 是 Object，客户端不知道 result 是什么类型，fastjson 序列化时用 WriteClassName 写入类型名
            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
            return response;
        } catch (Exception e) {
            // 3.Xstream
            // 2.封装一个统一的RpcfxException
            // 客户端也需要判断异常
            e.printStackTrace();
            response.setException(e);
            response.setStatus(false);
            return response;
        }
    }

    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
    }

}
