package site.shanzhao.soil.basis.nio.netty.delimiter_nonstick;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.swing.text.MaskFormatter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;

/**
 * @author tanruidong
 * @date 2021/01/18 21:37
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private int counter;
    private static final String ECHO_REQ = "Hi, TanRuiDong. Welcome to Netty.$_";

    public EchoClientHandler() {
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0;i < 10;i++){
            ByteBuf req = Unpooled.copiedBuffer(ECHO_REQ.getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(req);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取数据
        System.out.println("This is "+ ++counter + " times receive server :["+ msg +"]");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
