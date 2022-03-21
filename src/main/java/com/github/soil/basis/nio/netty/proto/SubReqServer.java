package com.github.soil.basis.nio.netty.proto;

import com.github.soil.basis.nio.netty.nonstick.TimeServer;
import com.github.soil.basis.nio.netty.nonstick.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author tanruidong
 * @date 2021/01/24 16:36
 */
public class SubReqServer {
    public static void main(String[] args) {
        int port = 8082;
        new SubReqServer().bind(port);
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
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new SubReqServerHandler());
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
