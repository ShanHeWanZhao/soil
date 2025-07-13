package com.github.soil.thread;

import java.util.concurrent.Semaphore;

/**
 * @author tanruidong
 * @since 2024/05/14 15:19
 */
public class ThreadAlternatePrint {
    public static void main(String[] args) {
        int n = 100;
        Semaphore semaphore1 = new Semaphore(1);
        Semaphore semaphore2 = new Semaphore(0);
        Semaphore semaphore3 = new Semaphore(0);
        Runnable run1 = () -> {
            int count = 0;
            while(count < n){
                try {
                    semaphore1.acquire();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + ": " + count++);
                semaphore2.release();
            }
        };
        Runnable run2 = () -> {
            int count = 0;
            while(count < n){
                try {
                    semaphore2.acquire();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + ": " + count++);
                semaphore3.release();
            }
        };
        Runnable run3 = () -> {
            int count = 0;
            while(count < n){
                try {
                    semaphore3.acquire();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + ": " + count++);
                semaphore1.release();
            }
        };
        new Thread(run1, "thread-1").start();
        new Thread(run2, "thread-2").start();
        new Thread(run3, "thread-3").start();
    }
}
