package com.github.soil.basis.thread;

/**
 * @author tanruidong
 * @date 2020/08/05 09:51
 */
public class InterruptTest {
    public static void main(String[] args) {
        int threadCount = 5;
        for (int i = 1;i <= threadCount; i++){
            Thread a = new Thread(() -> {
                System.out.println(Thread.currentThread().getName()+" running");
                System.out.println(Thread.interrupted());
                System.out.println(Thread.interrupted());
            },"interrupt: "+i);
            a.start();
            if (i == 3){
                a.interrupt();
            }
        }

    }
}
