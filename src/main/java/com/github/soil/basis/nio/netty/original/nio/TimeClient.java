package com.github.soil.basis.nio.netty.original.nio;

/**
 * @author tanruidong
 * @date 2021/01/16 22:29
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
    }
}
