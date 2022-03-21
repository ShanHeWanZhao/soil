package com.github.soil.basis.nio.netty.http;

import com.github.soil.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author tanruidong
 * @date 2021/01/30 12:43
 */
public abstract class AbstractHttpJsonEncoder<T> extends MessageToMessageEncoder<T> {
    protected ByteBuf encode0(ChannelHandlerContext ctx, Object body){
        String json = JsonUtils.object2String(body);
        return Unpooled.copiedBuffer(json, StandardCharsets.UTF_8);
    }
}
