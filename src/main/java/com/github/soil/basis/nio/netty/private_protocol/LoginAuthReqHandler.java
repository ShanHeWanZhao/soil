package com.github.soil.basis.nio.netty.private_protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tanruidong
 * @date 2021/02/19 12:44
 */
@Slf4j
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("通道激活，握手请求认证..................");

        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(ctx.handler());
        System.out.println(ctx.handler() == this);
        NettyMessage message = (NettyMessage) msg;

        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {

            byte loginResult = (byte) message.getBody();

            if (loginResult != ResultType.SUCCESS.value()) {
                // 握手失败，关闭连接
                ctx.close();
            } else {
                log.info("客户端认证成功，消息：{}", message);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
