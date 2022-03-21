package com.github.soil.basis.nio.netty.http;

import com.github.soil.basis.nio.netty.http.entity.Order;
import com.github.soil.basis.nio.netty.http.request.HttpJsonRequestEncoder;
import com.github.soil.basis.nio.netty.http.response.HttpJsonResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

/**
 * @author tanruidong
 * @date 2021/01/18 21:30
 */
public class HttpJsonClient {
    public static void main(String[] args) throws InterruptedException {
        int port = 8082;
        new HttpJsonClient().connect(port);
    }
    public void connect(int port) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpResponseDecoder())
                                    .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                    .addLast("json-decoder", new HttpJsonResponseDecoder(Order.class))
                                    .addLast("http-encoder", new HttpRequestEncoder())
                                    .addLast("json-encoder", new HttpJsonRequestEncoder())
                                    .addLast("jsonClientHandler", new HttpJsonClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(new InetSocketAddress(port)).sync();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

    }
}
