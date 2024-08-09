package com.nrxt.comm.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 打印接收到的消息
        System.out.println("Received message: " + msg);
        ctx.writeAndFlush("Server received: " + msg); // 向客户端发送响应
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 处理异常
        cause.printStackTrace();
        ctx.close(); // 关闭通道
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当连接建立时调用
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
        // 可以在这里执行一些初始化操作，例如发送欢迎消息等
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 当连接关闭时调用
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
        // 可以在这里执行一些清理操作，例如释放资源等
    }

}
