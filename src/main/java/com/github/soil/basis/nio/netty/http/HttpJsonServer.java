package com.github.soil.basis.nio.netty.http;

import com.github.soil.basis.nio.netty.http.entity.Order;
import com.github.soil.basis.nio.netty.http.request.HttpJsonRequestDecoder;
import com.github.soil.basis.nio.netty.http.response.HttpJsonResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author tanruidong
 * @date 2021/01/13 16:54
 */
public class HttpJsonServer {

    public static void main(String[] args) {
        int port = 8082;
        new HttpJsonServer().bind(port);
    }

    public void bind(int port) {
        // 服务端的NIO线程组
        NioEventLoopGroup bossGroups = new NioEventLoopGroup();
        NioEventLoopGroup workerGroups = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroups, workerGroups)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("http-decoder", new HttpRequestDecoder())
                                    .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                    .addLast("json-decoder", new HttpJsonRequestDecoder(Order.class))
                                    .addLast("http-encoder", new HttpResponseEncoder())
                                    .addLast("json-encoder", new HttpJsonResponseEncoder())
                                    .addLast("jsonClientHandler", new HttpJsonServerHandler());
                        }
                    });
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            System.out.println("HTTP 订购服务器启动，网址是："+"http://localhost:"+port);
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
