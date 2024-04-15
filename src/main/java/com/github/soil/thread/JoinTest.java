package com.github.soil.thread;

/**
 * @author tanruidong
 * @date 2020/07/20 23:01
 */
public class JoinTest {

    public static void main(String[] args) throws Exception {
        Runnable r = () -> {
            System.out.println("join 测试");
        };
        Thread t1 = new Thread(r, "thread-1");
        Thread t2 = new Thread(r, "thread-2");
        t1.start();
        t2.start();
        t1.join();
    }
}
