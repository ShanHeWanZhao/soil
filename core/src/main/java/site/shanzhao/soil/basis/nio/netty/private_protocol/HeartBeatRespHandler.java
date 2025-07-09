package site.shanzhao.soil.basis.nio.netty.private_protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tanruidong
 * @date 2021/02/19 13:09
 */
@Slf4j
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;

        // 判断是否 是心跳检测消息
        if (message.getHeader() != null && message.getHeader().getType() ==
                MessageType.HEARTBEAT_REQ.value()) {
            log.info("开始处理心跳检测请求");
            log.info("客户端发送过来的心跳检测消息 : ---> {} " ,message);
            NettyMessage heartBeat = buildHeatBeat();
            log.info("服务端发送的心跳检测消息 : ---> {}" ,heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    // 生成心跳检测消息
    private NettyMessage buildHeatBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }

}