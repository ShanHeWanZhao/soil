package com.github.soil.basis.nio.netty.http.response;

import com.github.soil.basis.nio.netty.http.AbstractHttpJsonEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.List;

/**
 * @author tanruidong
 * @date 2021/01/30 13:39
 */
public class HttpJsonResponseEncoder extends AbstractHttpJsonEncoder<HttpJsonResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, HttpJsonResponse msg, List<Object> out) throws Exception {
        ByteBuf body = encode0(ctx, msg.getResult());
        FullHttpResponse response = msg.getResponse();
        if (response == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, body);
        }else {
            response = new DefaultFullHttpResponse(msg.getResponse().protocolVersion(), msg.getResponse().status(), body);
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        HttpUtil.setContentLength(response, body.readableBytes());
        out.add(response);
    }
}
