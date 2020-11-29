package com.github.soil.basis.thread;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Thread.join()方法测试
 * @author tanruidong
 * @date 2020/07/23 12:12
 */
public class JoinTest {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Thread joinThread = new Thread(() -> {
            // 一直阻塞到这里
            while (true);
        });
        joinThread.start();
        int b = 0;
        for (long i = 0; i < 10; i++) {
            b--;
        }
        System.out.println("main :" + ( System.currentTimeMillis()-start)+"ms");
        // join方法测试
        joinThread.join();
        System.out.println(joinThread.getState());
    }
    @Test
    public void test_joinSequence() throws InterruptedException {
        Thread a = new Thread(() -> {
            System.out.println("a thread will sleep");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                // do nothing
            }
            System.out.println("a thread over");
        },"a");
        Thread b = new Thread(() -> {
            System.out.println("b thread will sleep a little");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // do nothing
            }
            System.out.println("b thread over");
        },"b");
        a.start();
        b.start();
        // join时会判断当前目标线程是否alive
        a.join();
        b.join();
        System.out.println("main thread over");
    }
}
