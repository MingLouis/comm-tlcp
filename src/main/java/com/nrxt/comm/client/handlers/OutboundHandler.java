package com.nrxt.comm.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 监测发送的数据
        System.out.println("Sending message: " + msg);

        // 调用下一个处理器
        super.write(ctx, msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        // 监测flush操作
        System.out.println("Flushing data");

        // 调用下一个处理器
        super.flush(ctx);
    }
}