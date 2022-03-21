package com.github.soil.basis.nio.netty.demo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoop;

import java.net.SocketAddress;

/**
 * @author tanruidong
 * @date 2021/02/21 11:04
 */
public class Outbound1Handler extends ChannelOutboundHandlerAdapter {

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NioEventLoop loop = (NioEventLoop) channel.eventLoop();
        String name = loop.threadProperties().name();
        String channelName = channel.getClass().getName() + ", " + channel.toString();
        String result = String.format("当前channel: [%s]，分配在了[%s] NioEventLoop中，所属线程名：[%s]", channelName, loop, name);
        System.out.println(result);
        super.read(ctx);
    }

}
