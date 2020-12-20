package io.kimmking.rpcfx.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.exception.RpcfxException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
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
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * rpc 客户端
 */
public final class RpcfxAop {

    static {
        // 反序列化全局配置
        ParserConfig.getGlobalInstance().addAccept("io.kimmking");
    }

    public static <T> T create(final Class<T> serviceClass, final String url) {
        // TODO 1.动态代理 -> AOP
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
//            RpcfxResponse response = postNettyclient(request, url);
            if (request!= null) {
                if (response.isStatus()) {
                    return JSON.parse(response.getResult().toString());
                } else {
                    throw new RpcfxException(response.getException());
                }
            }
            return null;
        }

        private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
            // 序列化为字符串
            String reqJson = JSON.toJSONString(req);
            System.out.println("OkHttpClient req json:" + reqJson);

            // TODO 4.复用client
            // TODO 3.尝试使用httpclient或者netty client
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSONTYPE, reqJson))
                    .build();
            // 返回 json 字符串
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("OkHttpClient resp json:"+respJson);
            // 第一次反序列化为 response 对象
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }

        /**
         * 使用 httpclient
         */
        private RpcfxResponse postHttpclient(RpcfxRequest req, String url) throws IOException {
            // 序列化为字符串
            String reqJson = JSON.toJSONString(req);
            System.out.println("Httpclient req json:" + reqJson);

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(new StringEntity(reqJson,"utf-8"));
            // 返回 json 字符串
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String respJson = httpClient.execute(httpPost, responseHandler);
            System.out.println("Httpclient resp json:"+respJson);
            // 第一次反序列化为 response 对象
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
        /**
         * TODO 待完成，使用 netty client
         */
        private RpcfxResponse postNettyclient(RpcfxRequest req, String url) throws IOException {
            // 序列化为字符串
            String reqJson = JSON.toJSONString(req);
            System.out.println("Nettyclient req json:" + reqJson);
            nettyclientConnet(reqJson, url);

            // 返回 json 字符串
            String respJson = null;
            System.out.println("Nettyclient resp json:"+respJson);
            // 第一次反序列化为 response 对象
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
        private void nettyclientConnet(String reqJson, String url) throws IOException {

            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 客户端接收 httpResponse 响应，要使用 HttpResponseDecoder 解码
                        ch.pipeline().addLast(new HttpResponseDecoder());
                        // 客户端发送 httpRequest，要使用 HttpRequestEncoder 编码
                        ch.pipeline().addLast(new HttpRequestEncoder());
                        ch.pipeline().addLast(new NettyHttpClientOutboundHandler());
                    }
                });
                String host = url.substring(0,url.lastIndexOf(":"));
                System.out.println(host);
                int port = Integer.valueOf(url.substring(url.lastIndexOf(":")+1,url.lastIndexOf("/")));
                System.out.println(port);
                ChannelFuture f = b.connect("127.0.0.1", port).sync();
                URI uri = new URI(url);

                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                        uri.toASCIIString(), Unpooled.wrappedBuffer(reqJson.getBytes()));
                request.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
                request.headers().add(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());

                f.channel().write(request);
                f.channel().flush();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        }
    }
}
