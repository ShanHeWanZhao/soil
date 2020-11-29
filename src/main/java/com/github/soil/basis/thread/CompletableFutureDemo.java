package com.github.soil.basis.thread;

import java.util.concurrent.*;

/**
 * @author tanruidong
 * @date 2020/11/27 11:41
 */
public class CompletableFutureDemo {
    private static final ExecutorService exec = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws Exception {
//       test1();
        test2();
    }

    private static void test2() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync方法线程："+Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (1 == 1){
                throw new RuntimeException("这是故意错误");
            }
            return "我是你爸爸";
        }, exec).whenComplete((s, throwable) -> {
            System.out.println("whenComplete方法线程："+Thread.currentThread().getName());
            System.out.println(s);
            System.out.println(throwable);
            exec.shutdown();
        });
        System.out.println("main方法线程："+Thread.currentThread().getName());
        System.out.println("main thread is done");
    }

    private static void test1() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "我是你爸爸";
        });
        System.out.println(future.get());
    }
}
