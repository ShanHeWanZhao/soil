package com.github.soil.basis.thread.threadlocal;

import java.util.concurrent.TimeUnit;

/**
 * @author tanruidong
 * @date 2020/11/19 22:02
 */
public class ThreadLocalDemo {
    public static void main(String[] args) throws InterruptedException {
        A a = firstStack();
        System.gc();
        TimeUnit.SECONDS.sleep(1);
        Thread thread = Thread.currentThread();
        System.out.println(thread);

    }
    // 通过是否获取返回值观察A对象里的local对象是否被回收
    private static A firstStack(){
        A a = new A();
        System.out.println("value: "+ a.get());
        return a;
    }
    public static class A{
        private ThreadLocal<String> local = ThreadLocal.withInitial(() -> "in class A");

        public String get(){
            return local.get();
        }
        public void set(String str){
            local.set(str);
        }

        public ThreadLocal getLocal(){
            return local;
        }

    }
}
