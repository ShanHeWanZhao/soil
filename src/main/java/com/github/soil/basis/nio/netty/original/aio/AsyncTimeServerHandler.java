package com.github.soil.basis.nio.netty.original.aio;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @author tanruidong
 * @date 2021/01/16 23:31
 */
public class AsyncTimeServerHandler implements Runnable{
    private int port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try{
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(port);
            asynchronousServerSocketChannel.bind(address);
            System.out.println("AIO Time Server is start address " + address);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        doAccept();
        try{
            latch.await();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }
}
