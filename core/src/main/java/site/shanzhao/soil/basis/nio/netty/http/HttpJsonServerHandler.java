package site.shanzhao.soil.basis.nio.netty.http;

import site.shanzhao.soil.basis.nio.netty.http.entity.Order;
import site.shanzhao.soil.basis.nio.netty.http.request.HttpJsonRequest;
import site.shanzhao.soil.basis.nio.netty.http.response.HttpJsonResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Collections;

/**
 * @author tanruidong
 * @date 2021/01/30 15:29
 */
public class HttpJsonServerHandler extends SimpleChannelInboundHandler<HttpJsonRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpJsonRequest msg) throws Exception {
        FullHttpRequest request = msg.getRequest();
        Order order = (Order) msg.getBody();
        System.out.println("Http server receive request : \n"+
                order);
        doBusiness(order);
        ChannelFuture future = ctx.writeAndFlush(new HttpJsonResponse(null, order));
        System.out.println("server端数据写出");
        if (!HttpUtil.isKeepAlive(request)){
            System.out.println("server关闭");
            future.addListener(future1 -> ctx.close());
        }
    }

    private void doBusiness(Order order){
        order.getCustomer()
                .setFirstName("狄")
                .setLastName("仁杰")
                .setMiddleName(Collections.singletonList("李元芳"));
        order.getBillTo()
                .setCity("洛阳")
                .setCountry("大唐")
                .setState("河南道")
                .setPostCode("123456");
        order.setBillTo(order.getBillTo());
        order.setShipTo(order.getBillTo());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()){
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("失败：" + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
