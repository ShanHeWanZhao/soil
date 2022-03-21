package com.github.soil.basis.nio.netty.http.request;

import com.github.soil.basis.nio.netty.http.AbstractHttpJsonDecoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @author tanruidong
 * @date 2021/01/30 15:00
 */
public class HttpJsonRequestDecoder extends AbstractHttpJsonDecoder<FullHttpRequest> {

    public HttpJsonRequestDecoder(Class<?> targetClass) {
        super(targetClass);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
        if (!msg.decoderResult().isSuccess()){
         sendError(ctx, HttpResponseStatus.BAD_REQUEST);
         return;
        }
        HttpJsonRequest request = new HttpJsonRequest(msg, decode0(msg.content()));
        out.add(request);
    }


    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("失败：" + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
