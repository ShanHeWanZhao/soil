package com.github.soil.basis.nio.netty.demo;

import com.github.soil.basis.nio.netty.private_protocol.NettyConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tanruidong
 * @date 2021/02/21 11:01
 */
@Slf4j
public class DemoServer {
    public static void main(String[] args) throws InterruptedException {
        new DemoServer().bind();
    }
    public void bind() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(3);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new Inbound1Handler())
                                .addLast(new Inbound2Handler())
                                .addLast(new Inbound3Handler())
                                .addLast(new Outbound1Handler())
                                .addLast(new Outbound2Handler());
                    }
                });
        b.bind(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT).sync();
        log.info("Netty server start ok : [{} : {}]", NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT);
    }
}
