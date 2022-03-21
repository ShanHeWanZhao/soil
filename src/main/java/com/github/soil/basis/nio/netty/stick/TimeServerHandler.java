package com.github.soil.basis.nio.netty.stick;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author tanruidong
 * @date 2021/01/20 20:26
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取数据
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, StandardCharsets.UTF_8).substring(0, req.length - System.getProperty("line.separator").length());
        System.out.println("The time server receive order: ["+body+"];the counter is : "+ ++counter);
        // 进行比较，确认返回数据
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? LocalDateTime.now().toString() : "BAD ORDER";
        currentTime = currentTime + System.getProperty("line.separator");
        // 封装返回数据到ByteBuf中
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes(StandardCharsets.UTF_8));
        // 写入数据并刷新缓冲区
        ctx.writeAndFlush(resp);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
