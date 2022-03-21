package com.github.soil.basis.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 测试wait和notify时线程的流转状态：
 * wait线程：  runnable(进入) -> waiting（调用wait()方法） -> blocked（notify线程调用了notify()方法） -> runnable（重新获取到锁）
 * notify线程：            runnable（进入） -> 开始调用notify -> 出栈 <p/>
 * 总结：notify()方法会把等待的线程从等待队列转入到阻塞队列，状态从waiting变成blocked
 * @author tanruidong
 * @date 2022/02/07 11:49
 */
public class WaitNotifyDemo {

    private static final Object lock = new Object();
    private static final CountDownLatch latch = new CountDownLatch(2);
    private static final CountDownLatch latch1 = new CountDownLatch(1);
    private static final String PREFIX = "demo-";

    public static void main(String[] args) throws InterruptedException {
        Thread waitThread = new Thread(() -> {
            try {
                lock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },PREFIX + "wait");
        Thread notifyThread = new Thread(() -> {
            try {
                unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, PREFIX + "notify");
        waitThread.start();
        TimeUnit.MILLISECONDS.sleep(200);
        printThreadState("wait线程已进入等待状态");
        notifyThread.start();
        printThreadState("释放notify线程的释放锁");
    }

    public static void lock() throws InterruptedException {
        synchronized (lock){
            lock.wait();
        }
    }

    public static void unlock() throws InterruptedException {
        synchronized (lock){
            lock.notify();
            printThreadState("start了全部线程且阻塞notify线程的释放锁");
        }
    }
    public static void printThreadState(String message){
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[mainGroup.activeCount()];
        Thread.currentThread().getThreadGroup().enumerate(threads);
        System.out.println("====================="+message+"========================");
        for (Thread thread : threads) {
            if (thread.getName().startsWith(PREFIX)){
                System.out.println(thread.getName() + ": " + thread.getState());
            }
        }
    }
}
