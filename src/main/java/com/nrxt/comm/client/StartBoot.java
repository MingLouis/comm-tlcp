package com.nrxt.comm.client;

import com.nrxt.comm.client.handlers.NettyClientHandler;
import com.nrxt.comm.tlcp.TLCPContext;
import com.tencent.kona.crypto.KonaCryptoProvider;
import com.tencent.kona.pkix.KonaPKIXProvider;
import com.tencent.kona.ssl.KonaSSLProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.security.Security;

public class StartBoot {
    private final static String host = "127.0.0.1";
    private final static int port = 9999;

    public static void main(String[] args) {
        startClient();
    }

    private static void startClient() {
        Security.insertProviderAt(new KonaCryptoProvider(), 1);
        Security.insertProviderAt(new KonaPKIXProvider(), 2);
        Security.insertProviderAt(new KonaSSLProvider(), 3);
        System.setProperty("com.tencent.kona.ssl.debug", "all");

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.group(group)
//                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.SO_KEEPALIVE, true)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
////                            ch.pipeline().addLast(new TLCPContext(true).newHandler(ch.alloc()));
//                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
//                            ch.pipeline().addLast(new StringDecoder());
//                            ch.pipeline().addLast(new StringEncoder());
//                            ch.pipeline().addLast(new NettyServerHandler());
//                            ch.pipeline().addLast(new OutboundHandler());
//                        }
//                    });

            bootstrap.group(group) // 设置线程组
                    .channel(NioSocketChannel.class) // 使用NioSocketChannel作为客户端通道实现
                    .option(ChannelOption.SO_KEEPALIVE, true) // 保持连接
                    .handler(new ChannelInitializer<SocketChannel>() { // 初始化通道
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO)); // 添加日志处理器
                            pipeline.addLast(new TLCPContext(true).newHandler(ch.alloc()));
//                            pipeline.addLast(new StringDecoder()); // 添加字符串解码器
//                            pipeline.addLast(new StringEncoder()); // 添加字符串编码器
                            pipeline.addLast(new NettyClientHandler()); // 添加自定义的处理器
                        }
                    });

            // 连接到服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();

            // 发送消息到服务器
//            future.channel().writeAndFlush(Unpooled.copiedBuffer("Hello from Netty Client!",  CharsetUtil.UTF_8)).sync();

            // 等待客户端关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
