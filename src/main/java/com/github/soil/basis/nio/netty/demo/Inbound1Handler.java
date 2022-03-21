package com.github.soil.basis.nio.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author tanruidong
 */
public class Inbound1Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        int preIndex = buf.readerIndex();
        buf.readBytes(bytes);
        int afterIndex = buf.readerIndex();
        System.out.println("Inbound 1 handler channelRead, msg is :["+new String(bytes,StandardCharsets.UTF_8)+"], beforeIndex: ["+preIndex+"], afterIndex: ["+afterIndex+"]");
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Inbound 1 handler channelActive");
        ByteBuf req = Unpooled.copiedBuffer("Inbound 1 send this message\n".getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(req);
    }


}
