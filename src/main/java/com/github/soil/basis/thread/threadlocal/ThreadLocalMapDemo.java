package com.github.soil.basis.thread.threadlocal;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tanruidong
 * @date 2020/11/18 17:47
 */
public class ThreadLocalMapDemo {

    private final int threadLocalHashCode = nextHashCode();

    private static AtomicInteger nextHashCode = new AtomicInteger();

    private static final int HASH_INCREMENT = 1640531527;

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    public static void main(String[] args) {
        int count = 1000;
        for(int i = 0;i < count;i++){
            System.out.println(new ThreadLocalMapDemo().threadLocalHashCode);
        }
    }
}
