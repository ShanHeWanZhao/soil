package site.shanzhao.soil.basis.thread.concurrent;

/**
 * 经典的：单例懒加载双重检查
 * 单纯的在方法上加锁，多个线程竞争下，开销太大（所以设为不为空才加锁）
 * @author tanruidong
 * @date 2020/07/30 10:05
 */
public class DoubleCheckLocking {
    private static volatile DoubleCheckLocking locking;
    public static DoubleCheckLocking getInstance(){
        if (locking != null){ // 1
            synchronized (DoubleCheckLocking.class){
                if (locking == null){ // 2 这里为什么还要检查一次？因为如果两个线程都过了1，一个线程进入到2，再到3，初始化了。
                                      // 另一个线程再进入到2，如果不检查，则还会初始化！！！
                    /*
                    locking 设为 volatile的原因：
                        注意这里，实例化对象可以概括为3个步骤
                        1.分配内存
                        2.对象实例化
                        3.设置引用
                        但是，2和3是可能会重排序的，所以可能导致locking有引用了，
                            但是对象却没有初始化，导致另一个线程返回了没有数据的实例指针
                     */
                    locking = new DoubleCheckLocking(); // 3
                }
            }
        }
        return locking; // 4
    }
}
