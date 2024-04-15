package com.github.soil.thread;

/**
 * @author tanruidong
 * @date 2020-05-29 12:26
 */
public class YieldTest {
    private static int count = 0;

    public static void main(String[] args) {
        Runnable r = () -> {
            int n = 100;
            while (n > 0) {
                n--;
                Thread thread = Thread.currentThread();
                if (thread.getName().equals("thread-1")) {
                    Thread.yield();
//                    boolean b = Thread.interrupted();
//                    System.out.println(b);
                }
                System.out.println(Thread.currentThread().getName() + ": count ->" + count + ", n ->" + n);
            }
        };
        Thread t1 = new Thread(r, "thread-1");
        Thread t2 = new Thread(r, "thread-2");
        Thread t3 = new Thread(r, "thread-3");
        Thread t4 = new Thread(r, "thread-4");
        Thread t5 = new Thread(r, "thread-5");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }

}
