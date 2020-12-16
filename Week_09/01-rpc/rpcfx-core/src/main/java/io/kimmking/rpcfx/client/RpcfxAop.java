package io.kimmking.rpcfx.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.exception.RpcfxException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * rpc 客户端
 */
public final class RpcfxAop {

    static {
        // 反序列化全局配置
        ParserConfig.getGlobalInstance().addAccept("io.kimmking");
    }

    public static <T> T create(final Class<T> serviceClass, final String url) {
        // 0. AOP
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(new RpcfxAopInvocationHandler(serviceClass, url));
        return (T) enhancer.create();
    }

    public static class RpcfxAopInvocationHandler implements MethodInterceptor {

        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        private final Class<?> serviceClass;
        private final String url;
        public <T> RpcfxAopInvocationHandler(Class<T> serviceClass, String url) {
            this.serviceClass = serviceClass;
            this.url = url;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(objects);

//            RpcfxResponse response = post(request, url);
            RpcfxResponse response = postHttpclient(request, url);
            System.out.println(response.isStatus());
            System.out.println("--------------------------");
            if (response.isStatus()) {
                return JSON.parse(response.getResult().toString());
            } else {
                throw new RpcfxException(response.getException());
            }
        }

        private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
            // 序列化为字符串
            String reqJson = JSON.toJSONString(req);
            System.out.println("req json:" + reqJson);

            // TODO 复用client
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

        /**
         * 使用 httpclient
         */
        private RpcfxResponse postHttpclient(RpcfxRequest req, String url) throws IOException {
            // 序列化为字符串
            String reqJson = JSON.toJSONString(req);
            System.out.println("req json:" + reqJson);

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(new StringEntity(reqJson,"utf-8"));
            // 返回 json 字符串
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String respJson = httpClient.execute(httpPost, responseHandler);
            System.out.println("resp json:"+respJson);
            // 第一次反序列化为 response 对象
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
        /**
         * TODO 使用 netty client
         */
        private RpcfxResponse postNettyclient(RpcfxRequest req, String url) throws IOException {
            // 序列化为字符串
            String reqJson = JSON.toJSONString(req);
            System.out.println("req json:" + reqJson);


            // 返回 json 字符串
            String respJson = null;
            System.out.println("resp json:"+respJson);
            // 第一次反序列化为 response 对象
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
    }
}
