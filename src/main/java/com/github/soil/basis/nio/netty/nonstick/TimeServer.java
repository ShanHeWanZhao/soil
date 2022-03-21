package com.github.soil.basis.nio.netty.nonstick;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 *  TCP解决粘包示例-服务端
 * @author tanruidong
 * @date 2021/01/13 16:54
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8082;
        new TimeServer().bind(port);
    }

    public void bind(int port) {
        // 服务端的NIO线程组
        NioEventLoopGroup bossGroups = new NioEventLoopGroup();
        NioEventLoopGroup workerGroups = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroups, workerGroups)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 相比原来，只是多增加了下面两个解码器(不能和下面的TimeServerHandler顺序交换)
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    });
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroups.shutdownGracefully();
            workerGroups.shutdownGracefully();
        }
    }

}
