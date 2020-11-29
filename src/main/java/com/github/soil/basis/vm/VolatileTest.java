package com.github.soil.basis.vm;

import java.util.concurrent.CountDownLatch;

/**
 * @author tanruidong
 * @date 2020/09/15 21:10
 */
public class VolatileTest {
    public static volatile int race = 0;

    public static void increase() {
        race++;
    }

    private static final int THREADS_COUNT = 20;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
        Thread[] threads = new Thread[THREADS_COUNT];
        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(() -> {
                for (int i1 = 0; i1 < 10000; i1++) {
                    increase();
                }
                latch.countDown();
            });
            threads[i].start();
        }
        // 等待所有累加线程都结束
        latch.await();
        System.out.println(race);
    }

}
