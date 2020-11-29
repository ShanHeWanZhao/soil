package com.github.soil.basis.vm;

import com.sun.scenario.effect.impl.prism.PrTexture;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * @author tanruidong
 * @date 2020/11/19 21:49
 */
public class WeakReferenceDemo {

    public static void main(String[] args) throws InterruptedException {
        WeakReference<A> reference = new WeakReference<>(new A());
        System.out.println("before g：" + reference.get());
        System.gc();
        // gc足够
        TimeUnit.SECONDS.sleep(2);
        System.out.println("after gc：" + reference.get());
    }

    private static class A{
        private Object o = new Object();
    }
}
