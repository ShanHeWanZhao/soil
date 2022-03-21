package com.github.soil.basis.nio.netty.proto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.sql.SQLOutput;

/**
 * @author tanruidong
 * @date 2021/01/24 16:45
 */
public class SubReqServerHandler  extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
        if ("TanRuiDong".equalsIgnoreCase(req.getUsername())){
            System.out.println("Service accept client subscribe req: ["+req.toString()+"]");
            ctx.writeAndFlush(resp(req.getSubReqId(),"0000", "Netty book order succeed, 3 days later, sent to the designated address"));
        }else {
            System.out.println("Service accept client error user: ["+req.toString()+"]");
            ctx.writeAndFlush(resp(req.getSubReqId(),"-1", "你丫谁啊？给我滚"));
        }
        super.channelRead(ctx, msg);
    }

    private SubscribeRespProto.SubscribeResp resp(int subReqId, String code, String desc){
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setSubReqId(subReqId)
                .setRespCode(code)
                .setDesc(desc);
        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
