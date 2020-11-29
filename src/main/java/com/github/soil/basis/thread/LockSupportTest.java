package com.github.soil.basis.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author tanruidong
 * @date 2020/08/01 14:08
 */
public class LockSupportTest {

    static String blocker = "blockerLock";
    volatile static boolean flag = true;
    public static void main(String[] args) throws InterruptedException {
        // jstack 指令多了一行：
        // - parking to wait for  <0x000000076ba966e8> (a java.lang.String)
//        Thread blockerThread = new Thread(() -> {
//            System.out.println("has blocker Thread start");
//            LockSupport.park(blocker);
//        }, "has blocker Thread");
//        Thread noBlockerThread = new Thread(() -> {
//            System.out.println("no blocker Thread start");
//            LockSupport.park();
//        }, "no blocker Thread");
//        Thread nanoParkThread = new Thread(() -> {
//            System.out.println("nano park Thread start");
//            LockSupport.parkNanos(1000 * 1000 * 1000 * 60L);
//            System.out.println("nano park Thread end");
//        }, "nano park Thread");
//        Thread untilParkThread = new Thread(() -> {
//            System.out.println("until park Thread start");
//            LockSupport.parkUntil(System.currentTimeMillis() + 1000 * 3L);
//            System.out.println("until park Thread end");
//        }, "until park Thread");
        Thread firstUnparkThread = new Thread(() -> {
            System.out.println("first unpark Thread start");
            while (flag);
            System.out.println("first park");
            LockSupport.park();
            System.out.println("second park");
            LockSupport.park();
            System.out.println("first unpark Thread end");
        }, "first unpark Thread");
//        blockerThread.start();
//        noBlockerThread.start();
//        nanoParkThread.start();
//        untilParkThread.start();
        firstUnparkThread.start();
        LockSupport.unpark(firstUnparkThread);
        System.out.println("first unpark thread unparked");
        TimeUnit.SECONDS.sleep(3);
        flag = false;
        System.out.println("first unpark thread parked");
    }
}
