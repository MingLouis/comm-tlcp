package com.nrxt.comm.client;

import com.nrxt.comm.client.handlers.NettyServerHandler;
import com.nrxt.comm.client.handlers.OutboundHandler;
import com.nrxt.comm.tlcp.TLCPContext;
import com.tencent.kona.crypto.KonaCryptoProvider;
import com.tencent.kona.pkix.KonaPKIXProvider;
import com.tencent.kona.ssl.KonaSSLProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

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
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TLCPContext(true).newHandler(ch.alloc()));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new NettyServerHandler());
                            ch.pipeline().addLast(new OutboundHandler());
                        }
                    });

            // 连接到服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();

            // 发送消息到服务器
            future.channel().writeAndFlush("Hello, Netty Server!");

            // 等待客户端关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
