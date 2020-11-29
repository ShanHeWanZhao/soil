package com.github.soil.basis.thread;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author tanruidong
 * @date 2020/08/03 10:15
 */
public class WaitNotifyTest {
    private static final Object lock = new Object();
    private static volatile boolean flag = true;

    public static void main(String[] args) throws Exception {
        WaitTask test = new WaitTask();
        for (int i = 1; i <= 10; i++) {
            Thread thread = new Thread(test, "wait-notify: " + i);
            thread.start();
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String line = scanner.nextLine();
            if (line.equals("na")){ // notifyAll waiting thread
                System.out.println("enter.....");
                synchronized (lock){
                    lock.notifyAll();
                }
                System.out.println("notify all");
            }else if (line.equals("f")){ // 先false，让线程不循环，在立马true，看看跑多少个
                flag = false;
                System.out.println("flag = "+ false);
                long start = System.nanoTime();
                // 看看这么短的是时间内跑了多少个线程（大概25个左右，在这个WaitTask里）
                TimeUnit.NANOSECONDS.sleep(1);
                long end = System.nanoTime();
                System.out.println("线程停顿到运行花费：【"+(end-start) /1000000.0 +"】 ms");
                flag = true;
                System.out.println("flag = "+ true);
            }
        }
    }

    public static class WaitTask implements Runnable {

        @Override
        public void run() {
            synchronized (lock){
                try{
                    System.out.println(Thread.currentThread().getName()+": enter");
                    lock.wait();
                    System.out.println(Thread.currentThread().getName()+": pass wait");
                    // 无线循环，观察线程状态
                    while (flag);
                    System.out.println(Thread.currentThread().getName()+": end");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
