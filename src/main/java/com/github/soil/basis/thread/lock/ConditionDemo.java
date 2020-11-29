package com.github.soil.basis.thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tanruidong
 * @date 2020/11/22 09:52
 */
public class ConditionDemo {
    private static final Lock lock = new ReentrantLock();
    private static Condition con = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        int count = 3;
        for (int i = 1; i<=count;i++){
            Thread thread = new Thread(() ->{
                try {
                    a();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },i + "");
            thread.start();
            TimeUnit.SECONDS.sleep(2);
        }
    }

    private static void a() throws InterruptedException {
        lock.lock();
        con.await();
        lock.unlock();
    }
}
