package com.nrxt.comm.client.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // 处理从服务器接收到的消息
        System.out.println("Received message from server: " + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 当连接建立时调用
        System.out.println("Connected to server: " + ctx.channel().remoteAddress());
        String response = "Hello from client!";
        ByteBuf out = Unpooled.copiedBuffer(response.getBytes());

        // 通过 ChannelHandlerContext 发送消息
        ctx.writeAndFlush(out);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // 当连接断开时调用
        System.out.println("Disconnected from server: " + ctx.channel().remoteAddress());
        // 可以在这里尝试重连或者其他逻辑
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 处理异常
        cause.printStackTrace();
        ctx.close();
    }


}
