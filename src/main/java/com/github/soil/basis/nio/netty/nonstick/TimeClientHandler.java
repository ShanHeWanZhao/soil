package com.github.soil.basis.nio.netty.nonstick;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author tanruidong
 * @date 2021/01/18 21:37
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private final byte[] req;
    private int counter;

    public TimeClientHandler() {
        req = ("QUERY TIME ORDER"+System.getProperty("line.separator"))
                .getBytes(StandardCharsets.UTF_8);

    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        for (int i = 0;i < 100;i++){
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取数据
        String body = (String) msg;
        System.out.println("Now is ["+body+"];the counter is : "+ ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
