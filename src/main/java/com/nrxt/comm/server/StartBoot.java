package com.nrxt.comm.server;

import com.nrxt.comm.server.handlers.NettyServerHandler;
import com.tencent.kona.crypto.KonaCryptoProvider;
import com.tencent.kona.pkix.KonaPKIXProvider;
import com.tencent.kona.ssl.KonaSSLProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.nrxt.comm.tlcp.TLCPContext;

import java.security.Security;

@Component
public class StartBoot{
    private final int port = 9999;


    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 自启动逻辑
        System.out.println("Application is ready and MyEventListener is running!");
        startServer();
    }

    private void setProviders() {
        Security.insertProviderAt(new KonaCryptoProvider(), 1);
        Security.insertProviderAt(new KonaPKIXProvider(), 2);
        Security.insertProviderAt(new KonaSSLProvider(), 3);
    }

    private void startServer() {
        setProviders();
        System.setProperty("com.tencent.kona.ssl.debug", "all");
        // 创建bossGroup和workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 处理连接请求
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理具体的业务

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap(); // 用于启动服务器的类
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 设置NIO传输模式
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //加入SSL管理器
                            ch.pipeline().addLast(new TLCPContext().newHandler(ch.alloc()));
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO)); // 添加日志处理器
//                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
//                            ch.pipeline().addLast(new StringDecoder());
//                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持连接

            // 绑定端口并启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("Server started on port: " + port);

            // 等待服务器关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭事件循环组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }



}
