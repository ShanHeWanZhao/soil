package com.github.soil.basis.nio.netty.http;

import com.github.soil.basis.nio.netty.http.entity.OrderFactory;
import com.github.soil.basis.nio.netty.http.request.HttpJsonRequest;
import com.github.soil.basis.nio.netty.http.response.HttpJsonResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author tanruidong
 * @date 2021/01/30 15:13
 */
public class HttpJsonClientHandler extends SimpleChannelInboundHandler<HttpJsonResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HttpJsonRequest request = new HttpJsonRequest(null, OrderFactory.create(123));
        ctx.writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpJsonResponse msg) throws Exception {
        System.out.println("The client receive response of http header is :\n"+
                msg.getResponse().headers().names());
        System.out.println("The client receive response of http body is :\n"+
                msg.getResult());
    }
}
