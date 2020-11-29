package com.github.soil.basis.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author tanruidong
 * @date 2020/06/21 17:53
 */
public class NonBlockServer {
    @Test
    public void server() throws IOException {
        // 创建服务端通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        // 切换为非阻塞模式
        ssChannel.configureBlocking(false);
        // 绑定port
        ssChannel.bind(new InetSocketAddress(8080));
        // 获取Selector
        Selector selector = Selector.open();
        // 将channel注册到Selector上，并指定监听“接收事件”
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 轮询获取Selector上已经“准备就绪”的时间(selector.select()方法是阻塞的)
        while (selector.select() > 0) {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while ((it.hasNext())) {
                // 获取准备就绪的事件
                SelectionKey next = it.next();
                // 判断是是什么事件
                if (next.isAcceptable()) {
                    // Accept就绪,获取客户端的Channel
                    SocketChannel sChannel = ssChannel.accept();
                    // 不阻塞
                    sChannel.configureBlocking(false);
                    // 将客户端的Channel注册到Selector上，监听Read事件
                    sChannel.register(selector, SelectionKey.OP_READ);
                } else if (next.isReadable()) {
                    // 获取当前Selector上就绪状态的Read事件的客户端Channel
                    SocketChannel sChannel = (SocketChannel) next.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (sChannel.read(buffer) > 0) {
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, buffer.limit()));
                        buffer.clear();
                    }
                }
                // 移除SelectKey
                it.remove();
            }
        }
    }

    @Test
    public void test(){
        char a = ' ';
        String b = " ";
        String c = "    ";
        String d = "\n";
        String e = "\t";
        String f = "\n\t";
        String g = "\r\n";
        String h = "\r";
        System.out.println(d+"q");
        System.out.println(e+"q");
        System.out.println(f+"q");
        System.out.println(g+"q");
        System.out.println(h+"q");
    }

}
