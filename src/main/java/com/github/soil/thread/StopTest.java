package com.github.soil.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tanruidong
 * @since 2024/04/13 00:25
 */
public class StopTest {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        try {
            Runnable run = () -> {
                int count = 0;
                while (true){
                    try {
                        System.out.println("运行了[" + ++count + "]次");
                        Thread.sleep(1000);
                    } catch (Throwable ex) {
                        System.out.println("Caught in inner: " + ex);
                    }
                }
            };
            Thread innerThread = new Thread(run, "inner");
            innerThread.start();
            Thread.sleep(1000);
            //停止线程的运行
            innerThread.stop();
            Thread.sleep(1000);
            System.out.println("inner线程是否还存活：" + innerThread.isAlive());
        } catch (Throwable t) {
            System.out.println("Caught in main: " + t);
        }

    }
}
