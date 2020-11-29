package com.github.soil.basis.thread;

import java.util.concurrent.locks.LockSupport;

/**
 * @author tanruidong
 * @date 2020/08/01 13:36
 */
public class LockSupportDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            // 阻塞当前线程
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + "被唤醒");
        });
        thread.start();
        try {
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName()+"： 醒了");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 唤醒指定的线程
        LockSupport.unpark(thread);
    }
}
