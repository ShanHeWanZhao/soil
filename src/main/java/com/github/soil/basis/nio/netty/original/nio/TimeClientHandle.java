package com.github.soil.basis.nio.netty.original.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * @author tanruidong
 * @date 2021/01/16 22:30
 */
public class TimeClientHandle implements Runnable{

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandle(String host, int port) {
        this.host = host == null ? "127.0.0.1" : host;
        this.port = port;
        try{
         selector = Selector.open();
         socketChannel = SocketChannel.open();
         socketChannel.configureBlocking(false);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try{
            doConnect();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop){
            try {
                selector.select(1000);
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                if (it.hasNext()){
                    System.out.println("监听存在");
                }
                SelectionKey key = null;
                while (it.hasNext()){
                    key = it.next();
                    System.out.println(key);
                    it.remove();
                    try{
                        handleInput(key);
                    }catch(Exception e){
                        e.printStackTrace();
                        if (key != null){
                            key.cancel();
                            if (key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        if (selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将SocketChannel注册到指定地址上
     * @throws IOException
     */
    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress(host, port))){ // 成功注册，监听可读操作
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else { // 注册地址失败，注册到Selector上监听可连接操作
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    /**
     * 向SocketChannel写入数据
     * @param sc
     * @throws IOException
     */
    private void doWrite(SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (writeBuffer.hasRemaining()){
            System.out.println("Send order 2 server succeed.");
        }
    }

    /**
     * 处理注册监听事件
     * @param key
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()){ // 如果可以建立连接
                System.out.println("服务端当前可建立连接");
                if (sc.finishConnect()){
                    sc.register(selector, SelectionKey.OP_READ);// 监听sc的可读时刻
                    doWrite(sc);
                }else {
                    // 连接失败，进程退出
                    System.exit(1);
                }
            }
            if (key.isReadable()){ // 此时可以从SocketChannel中读取除服务端发送来的消息
                System.out.println("客户端当前可Read");
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    // ByteBuffer对象翻转为读取模式
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("Now is : [" + body + "]");
                    this.stop = true;
                } else if (readBytes < 0) {
                    // 关闭端链路
                    key.cancel();
                    sc.close();
                } else {
                    System.out.println("读取到0字节，忽略");
                }
            }
        }
    }
}
