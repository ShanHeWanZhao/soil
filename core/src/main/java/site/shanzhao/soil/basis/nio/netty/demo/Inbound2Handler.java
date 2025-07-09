package site.shanzhao.soil.basis.nio.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author tanruidong
 * @date 2021/02/21 11:03
 */
public class Inbound2Handler  extends ChannelInboundHandlerAdapter {
    private int count = 0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Inbound 2 handler channelRead");
        ByteBuf byteBuf = Unpooled.copiedBuffer(("Inbound 2 handler send "+ ++count +" message").getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(byteBuf);
    }
}
