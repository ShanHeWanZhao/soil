package com.github.soil.basis.nio.netty.proto;

import com.github.soil.basis.thread.SleepSort;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tanruidong
 * @date 2021/01/25 20:33
 */
public class SubReqClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 1;i < 10;i++){
            if (i == 7 || i == 3){
                ctx.writeAndFlush(subReq(i, "xiaoqiang"));
            }else {
                ctx.writeAndFlush(subReq(i, "tanruidong"));
            }
        }
        ctx.flush();
    }

    private SubscribeReqProto.SubscribeReq subReq(int i, String user){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        return builder.setSubReqId(i)
                .setUsername(user)
                .setProductName("Netty Book For Protobuf")
                .addAllAddress(Arrays.asList("ChengDu TianFuSanJie", "BeiJing")).build();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server response: ["+msg+"]");
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
