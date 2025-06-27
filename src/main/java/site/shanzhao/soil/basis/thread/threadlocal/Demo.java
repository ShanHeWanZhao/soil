package site.shanzhao.soil.basis.thread.threadlocal;

import java.util.concurrent.TimeUnit;

/**
 * @author tanruidong
 * @date 2020/11/21 14:29
 */
public class Demo {
    public static void main(String[] args) throws InterruptedException {
        c();
        System.gc();
        TimeUnit.SECONDS.sleep(1);
    }
    private static ThreadLocal a(){
        ThreadLocalDemo.A a = new ThreadLocalDemo.A();
        System.out.println(a.get());
        return a.getLocal();
    }
    private static void c(){
        ThreadLocal a = a();
        System.out.println(a.get());
    }
}
