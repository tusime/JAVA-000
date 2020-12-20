package io.kimmking.rpcfx.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * rpc 客户端
 */
public final class Rpcfx {

    static {
        // 反序列化全局配置
        // fastjson高版本添加的功能，反序列化时能够指定类型存在安全问题（例如远程classload添加操作），高版本默认只有白名单中的类可被指定
        ParserConfig.getGlobalInstance().addAccept("io.kimmking");
    }

    public static <T> T create(final Class<T> serviceClass, final String url) {
        // 0. 替换动态代理 -> AOP
        // ClassLoader 加载该类，代理对象实现的数组，处理器-自动调用 RpcfxInvocationHandler 实现了 InvocationHandler 的 invoke 方法
        return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, new RpcfxInvocationHandler(serviceClass, url));

    }

    public static class RpcfxInvocationHandler implements InvocationHandler {

        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        private final Class<?> serviceClass;
        private final String url;
        public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url) {
            this.serviceClass = serviceClass;
            this.url = url;
        }

        // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
        // int byte char float double long bool
        // [], data class

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
            // 构造请求
            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(params);

            RpcfxResponse response = post(request, url);

            // 这里判断response.status，处理异常
            // 考虑封装一个全局的RpcfxException
            // 第二次反序列化，result 为 object 通过反序列化为 type 指定类
            return JSON.parse(response.getResult().toString());
        }

        private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
            // 序列化为字符串
            String reqJson = JSON.toJSONString(req);
            System.out.println("req json:" + reqJson);

            // 1.可以复用client
            // 2.尝试使用httpclient或者netty client
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSONTYPE, reqJson))
                    .build();
            // 返回 json 字符串
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("resp json:"+respJson);
            // 第一次反序列化为 response 对象
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
    }
}
