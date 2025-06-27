package site.shanzhao.soil.basis.nio.netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import site.shanzhao.soil.basis.nio.netty.private_protocol.NettyConstant;

import java.net.InetSocketAddress;

/**
 * @author tanruidong
 * @date 2021/02/19 13:14
 */
public class DemoClient {
    private NioEventLoopGroup group = new NioEventLoopGroup();

    public static void main(String[] args) {
        new DemoClient().connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
    }
    public void connect(int port, String host){
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new Inbound1Handler())
                                    .addLast(new Inbound2Handler())
                                    .addLast(new Inbound3Handler())
                                    .addLast(new Outbound1Handler())
                                    .addLast(new Outbound2Handler());
                        }
                    });
            ChannelFuture future = b.connect(new InetSocketAddress(host, port),
                    new InetSocketAddress(NettyConstant.LOCAL_IP, NettyConstant.LOCAL_PORT)).sync();
            future.channel().closeFuture().sync();
        }catch(Exception e){
          e.printStackTrace();
        }
    }
}
