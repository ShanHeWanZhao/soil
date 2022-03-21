package com.github.soil.basis.nio.netty.private_protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tanruidong
 * @date 2021/02/19 13:28
 */
@Slf4j
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().bind();
    }
    public void bind() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new NettyMessageDecoder(1024*1024, 4,4, -8))
                                .addLast(new NettyMessageEncoder())
                                .addLast("readTimeoutHandler", new ReadTimeoutHandler(50))
                                .addLast(new LoginAuthRespHandler())
                                .addLast("HeartBeatHandler", new HeartBeatRespHandler());
                    }
                });
        b.bind(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT).sync();
        log.info("Netty server start ok : [{} : {}]", NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT);
    }
}
