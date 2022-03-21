package com.github.soil.basis.nio.netty.private_protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingEncoder;

/**
 * @author tanruidong
 * @date 2021/02/19 16:30
 */
public class NettyMarshallingEncoder extends MarshallingEncoder{

    public NettyMarshallingEncoder(MarshallerProvider provider) {
        super(provider);
    }

    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception{
        super.encode(ctx, msg, out);
    }

}
