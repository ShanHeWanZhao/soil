package com.github.soil.basis.nio.netty.original.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * @author tanruidong
 * @date 2021/01/18 19:42
 */
public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>,Runnable {
    private AsynchronousSocketChannel client;
    private String host;
    private int port;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes(StandardCharsets.UTF_8);
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        System.out.println("默认编码集："+ Charset.defaultCharset().name());
        System.out.println("客户端开始写入数据，数据的长度为："+writeBuffer.remaining());
        client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()){
                    client.write(buffer, buffer, this);
                }else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer buffer) {
                            buffer.flip();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            String body = new String(bytes, StandardCharsets.UTF_8);
                            System.out.println("Now is : "+body);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                System.out.println("客户端读取服务端数据失败");
                                client.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            latch.countDown();
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
            });
        }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        latch.countDown();
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        client.connect(new InetSocketAddress(host, port), this, this);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
