package com.github.soil.basis.nio.netty.private_protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author tanruidong
 * @date 2021/02/19 13:14
 */
public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private NioEventLoopGroup group = new NioEventLoopGroup();

    public static void main(String[] args) {
        new NettyClient().connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
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
                                    .addLast(new NettyMessageDecoder(1024*1024, 4 ,4, -8))
                                    .addLast("MessageEncoder", new NettyMessageEncoder())
                                    .addLast("readTimeoutHandler", new ReadTimeoutHandler(50))
                                    .addLast("LoginAuthHandler", new LoginAuthReqHandler())
                                    .addLast("HeartBeatHandler", new HeartBeatReqHandler());
                        }
                    });
            ChannelFuture future = b.connect(new InetSocketAddress(host, port),
                    new InetSocketAddress(NettyConstant.LOCAL_IP, NettyConstant.LOCAL_PORT)).sync();
            future.channel().closeFuture().sync();
        }catch(Exception e){
          e.printStackTrace();
        }finally {
            executor.execute(() -> {
                try{
                    TimeUnit.SECONDS.sleep(5);
                    connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            });
        }
    }
}
