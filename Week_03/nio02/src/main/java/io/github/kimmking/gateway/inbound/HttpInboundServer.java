package io.github.kimmking.gateway.inbound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpInboundServer {
    private static Logger logger = LoggerFactory.getLogger(HttpInboundServer.class);

    private int port;
    
    private String proxyServer;

    public HttpInboundServer(int port, String proxyServer) {
        this.port=port;
        this.proxyServer = proxyServer;
    }

    public void run() throws Exception {
        // 线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);

        try {
            // 服务端启动线程，开启 socket
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 128) // 等待处理的连接请求队列大小，liunx下默认128，windows下默认200
                    .option(ChannelOption.TCP_NODELAY, true) // 开启Nagle算法，降低延迟
                    .option(ChannelOption.SO_KEEPALIVE, true) // 长连接
                    .option(ChannelOption.SO_REUSEADDR, true) // 端口释放后立即再次使用
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024) // 缓冲区大小
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .option(EpollChannelOption.SO_REUSEPORT, true) // 允许多个socket绑定到相同的地址和端口
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 配置子项：Group、Channel
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT); // 指定ByteBuf内存池，复用
            // 绑定Group，配置nio channel，handle
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new HttpInboundInitializer(this.proxyServer));

            Channel ch = b.bind(port).sync().channel();
            logger.info("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
