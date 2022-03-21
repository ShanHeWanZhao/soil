package com.github.soil.basis.nio.netty.fixed_nonstick;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *  TCP解决粘包示例-服务端
 *  固定长度字符解析器
 *  使用telnet测试
 *  telnet localhost 8082
 *  按ctrl+]显示命令行
 * @author tanruidong
 * @date 2021/01/13 16:54
 */
public class EchoServer {

    public static void main(String[] args) {
        int port = 8082;
        new EchoServer().bind(port);
    }

    public void bind(int port) {
        NioEventLoopGroup bossGroups = new NioEventLoopGroup();
        NioEventLoopGroup workerGroups = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroups, workerGroups)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new FixedLengthFrameDecoder(10))
                                    .addLast(new StringDecoder())
                                    .addLast(new EchoServerHandler());
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
