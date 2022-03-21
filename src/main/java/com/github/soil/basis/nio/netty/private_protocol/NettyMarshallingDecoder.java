package com.github.soil.basis.nio.netty.private_protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;

/**
 * @author tanruidong
 * @date 2021/02/19 16:29
 */
public class NettyMarshallingDecoder extends MarshallingDecoder{

    public NettyMarshallingDecoder(UnmarshallerProvider provider) {
        super(provider);
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }

}
