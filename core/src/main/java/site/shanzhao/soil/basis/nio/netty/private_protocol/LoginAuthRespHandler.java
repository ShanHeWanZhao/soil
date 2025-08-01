package site.shanzhao.soil.basis.nio.netty.private_protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanruidong
 * @date 2021/02/19 12:55
 */
@Slf4j
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    /**
     * 考虑到安全，链路的建立需要通过基于IP地址或者号段的黑白名单安全认证机制，本例中，多个IP通过逗号隔开
     */
    private final Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
    private final String[] whiteList = { "127.0.0.1", "192.168.206.29","192.168.1.109" };

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;
        // 判断消息是否为握手请求消息
        if (message.getHeader() != null && message.getHeader().getType()
                == MessageType.LOGIN_REQ.value()) {
            log.info("开始处理登录请求");
            String nodeIndex = ctx.channel().remoteAddress().toString();
            log.info("客户端的SocketAddress："+nodeIndex);
            NettyMessage loginResp = null;
            if (nodeCheck.containsKey(nodeIndex)) {
                log.error("重复登录,拒绝请求!");
                loginResp = buildResponse(ResultType.FAIL);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                log.info("客户端的ip："+ip);
                boolean isOK = false;
                for (String WIP : whiteList) {
                    if (WIP.equals(ip)) {
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK ? buildResponse(ResultType.SUCCESS) : buildResponse(ResultType.FAIL);
                if (isOK)
                    nodeCheck.put(nodeIndex, true);
            }
            log.info("The login response is : {} body [{}]",loginResp,loginResp.getBody());
            ctx.writeAndFlush(loginResp);
        } else {
            ctx.fireChannelRead(msg);

        }
    }

    /**
     * 服务端接到客户端的握手请求消息后，如果IP校验通过，返回握手成功应答消息给客户端，应用层成功建立链路，否则返回验证失败信息。消息格式如下：
     * 1.消息头的type为4
     * 2.可选附件个数为0
     * 3.消息体为byte类型的结果，0表示认证成功，1表示认证失败
     */
    private NettyMessage buildResponse(ResultType result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result.value());
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        nodeCheck.remove(ctx.channel().remoteAddress().toString());// 删除缓存
        ctx.close();
        ctx.fireExceptionCaught(cause);    }
}
