package com.github.soil.basis.nio.netty.http.request;

import com.github.soil.basis.nio.netty.http.AbstractHttpJsonEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.net.InetAddress;
import java.util.List;

/**
 * @author tanruidong
 * @date 2021/01/30 13:06
 */
public class HttpJsonRequestEncoder extends AbstractHttpJsonEncoder<HttpJsonRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, HttpJsonRequest msg, List<Object> out) throws Exception {
        ByteBuf body = encode0(ctx, msg.getBody());
        FullHttpRequest request = msg.getRequest();
        if (request == null){
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "do",body);
            HttpHeaders headers = request.headers();
            headers.set(HttpHeaderNames.HOST, InetAddress.getLocalHost().getHostAddress());
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP.toString()
            +","+HttpHeaderValues.DEFLATE.toString());
            headers.set(HttpHeaderNames.ACCEPT_LANGUAGE,"zh");
            headers.set(HttpHeaderNames.ACCEPT_CHARSET,"ISO-8858-1,utf-8;q=0.7,*;q=0.7");
            headers.set(HttpHeaderNames.USER_AGENT,"Netty Json Http Client side");
            headers.set(HttpHeaderNames.ACCEPT,"application/json,text/html,application/xhtml+xml");
        }
        HttpUtil.setContentLength(request, body.readableBytes());
        out.add(request);
    }
}
