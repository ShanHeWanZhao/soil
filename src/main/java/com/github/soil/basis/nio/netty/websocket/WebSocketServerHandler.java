package com.github.soil.basis.nio.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author tanruidong
 * @date 2021/01/13 17:17
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest){ // 第一次握手请求是http协议的
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }else if (msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }



    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req){
        // http解码失败，返回http异常
        if (!req.decoderResult().isSuccess() || !"websocket".equals(req.headers().get("Upgrade"))){
            System.out.println("http解码失败");
            req.headers().iteratorAsString().forEachRemaining(s ->
                    {
                        String result = s.getKey() + "="+s.getValue();
                        System.out.println(result);
                    });
            sendHttpResp(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 构造握手响应返回，本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8082/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // 是否是关闭链路指令
        if (frame instanceof CloseWebSocketFrame){
            System.out.println("关闭");
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            return;
        }
        // 是否是ping消息
        if (frame instanceof PingWebSocketFrame){
            System.out.println("ping");
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例仅支持文本消息，不支持二进制消息
        if (! (frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(frame.getClass().getName() +" frame types not support");
        }
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println(String.format("[%s] received [%s]", ctx.channel(), request));
        ctx.channel().write(new TextWebSocketFrame(request +"[欢迎使用Netty WebSocket服务，现在时刻："+formatter.format(LocalDateTime.now())+"]"));
    }

    private static void sendHttpResp(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res){
        if (res.status().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        // 非keep-alive，就关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200 ){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
