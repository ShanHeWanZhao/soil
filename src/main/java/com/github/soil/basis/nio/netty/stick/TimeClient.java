package com.github.soil.basis.nio.netty.stick;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 *  TCP粘包示例-客户端
 * @author tanruidong
 * @date 2021/01/18 21:30
 */
public class TimeClient {
    public static void main(String[] args) throws InterruptedException {
        int port = 8082;
        new TimeClient().connect(port, "127.0.0.1");
    }
    public void connect(int port, String host) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

    }
}
