package io.kimmking.rpcfx.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.exception.RpcfxException;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * rpc 服务端
 */
public class RpcfxReflectInvoker {

    private RpcfxResolver resolver;

    public RpcfxReflectInvoker(RpcfxResolver resolver){
        this.resolver = resolver;
    }

    // request 获取 rpc 三要素，getServiceClass接口名，getMethod方法，getParams参数
    public RpcfxResponse invoke(RpcfxRequest request) {
        RpcfxResponse response = new RpcfxResponse();
        String serviceClass = request.getServiceClass();

        try {
            // TODO 报错 作业1：改成泛型和反射
            Class service = Class.forName(serviceClass);
            // 反射获得方法
            Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
            Object result = method.invoke(service, request.getParams());
            // 可以将两次json序列化能否合并成一个？将@RestController去掉，统一使用fastjson
            // 序列化object->json字符串，spring 再序列化（@RestController的作用）
            // RpcfxResponse 的 result 是 Object，客户端不知道 result 具体是什么类型，fastjson序列化时用 WriteClassName 写入类型名
            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
            return response;
        } catch (Exception e) {
            // 3.Xstream
            // 2.封装一个统一的RpcfxException
            // 客户端也需要判断异常
            e.printStackTrace();
            response.setException(new RpcfxException("an error has occurred on the rpc server!"));
            response.setStatus(false);
            return response;
        }
    }

    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
    }

}
