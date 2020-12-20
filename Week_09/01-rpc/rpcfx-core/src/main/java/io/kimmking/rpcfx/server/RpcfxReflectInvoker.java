package io.kimmking.rpcfx.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.exception.RpcfxException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
//            int i=1/0;
            // TODO 2.改成泛型和反射
            Object service = resolver.resolve(serviceClass);
            // Class.forName 获取为接口对象
//            Class service = Class.forName(serviceClass);
//            Method[] methods = service.getMethods();
//            Method method = null;
//            for(Method m:methods) {
//                System.out.println(m);
//                System.out.println(m.getDeclaringClass());
//                System.out.println(m.getParameterTypes());
//                if(request.getMethod().equals(m.getName())) {
//                    method = m;
//                }
//            }
            // 获得接口后，获取接口实现类
//            Class c = Class.forName(serviceClass);
//            Class service = resolveClassFromInterface(c);

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
//        Method[] methods = klass.getMethods();
//        for(Method method:methods) {
//            if(methodName.equals(method.getName())) {
//                return method;
//            }
//        }
        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
    }

    /**
     * 参照 https://my.oschina.net/u/3985891/blog/3106326 获取所有实现类，获取第一个实现类 (ㄒoㄒ)，有点怪暂不写
     */
    private <T> Class<T> resolveClassFromInterface(Class<T> klass) {
        List<Class<T>> list = new ArrayList<>();
        // 判断class对象是否是一个接口
        if (klass.isInterface()) {
            Class<?>[] cs = klass.getInterfaces();
            for(Class c:cs) {
                list.add(c);
            }
            return list.get(0);
        }
        return klass;
    }
}
