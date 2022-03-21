package com.github.soil.basis.thread.scheduled;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 测试依次向定时任务线程池添加最快需要执行的时间，可能会存在多个TIMED_WAITING的线程，
 * 每一个TIMED_WAITING线程至少都成为过leader，然后可能又被取消了
 * @author tanruidong
 * @date 2021/12/15 14:42
 */
public class ScheduledPoolLeaderThread {

    private static final String threadPrefix1 = "ScheduledPool-addFastest";
    private static final String threadPrefix2 = "ScheduledPool-addSlowest";

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 5;
        // 设为后台线程，main线程结束就直接退出程序，因为我们只是观察线程状态的，而不是真正要执行定时任务
        ScheduledThreadPoolExecutor fastest = new ScheduledThreadPoolExecutor(threadCount, new DefaultThreadFactory(threadPrefix1, true));
        ScheduledThreadPoolExecutor slowest = new ScheduledThreadPoolExecutor(threadCount, new DefaultThreadFactory(threadPrefix2, true));
        for (int i = 20; i >= 5; i--) {
            fastest.scheduleAtFixedRate(() -> System.out.println(Thread.currentThread().getName() + ": 任务执行了"),
                    i, 2, TimeUnit.MINUTES);
        }
        for (int i = 5; i <= 20; i++) {
            slowest.scheduleAtFixedRate(() -> System.out.println(Thread.currentThread().getName() + ": 任务执行了"),
                    i, 2, TimeUnit.MINUTES);
        }
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[mainGroup.activeCount()];
        Thread.currentThread().getThreadGroup().enumerate(threads);
        System.out.println("==========线程池fastest（每次添加最快需要执行的任务）==========");
        for (Thread thread : threads) {
            if (thread.getName().startsWith(threadPrefix1)){ // 这个线程池可能存在多个TIMED_WAITING的线程
                System.out.println(thread.getName() + ": " + thread.getState());
            }
        }
        System.out.println("=========线程池slowest（每次添加最慢需要执行的任务）=============");
        for (Thread thread : threads) {
            if (thread.getName().startsWith(threadPrefix2)){// 这个线程池无论怎样都只有一个TIMED_WAITING线程
                System.out.println(thread.getName() + ": " + thread.getState());
            }
        }
    }
}
