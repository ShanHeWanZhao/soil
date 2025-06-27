package site.shanzhao.soil.basis.nio.netty.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author tanruidong
 * @date 2021/02/21 11:03
 */
public class Inbound3Handler  extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Inbound 3 handler channelRead");
    }
}
