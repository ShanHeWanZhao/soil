package site.shanzhao.soil.basis.thread.concurrent;

/**
 * 使用jps和jstack pid 查看该APP的所有线程状态
 * @author tanruidong
 * @date 2020/07/30 11:12
 */
public class ThreadState {
        public static void main(String[] args) {
            new Thread(new TimeWaiting (), "TimeWaitingThread").start();
            new Thread(new Waiting(), "WaitingThread").start();
            // 使用两个Blocked线程，一个获取锁成功，另一个被阻塞
            new Thread(new Blocked(), "BlockedThread-1").start();
            new Thread(new Blocked(), "BlockedThread-2").start();
        }
        // 该线程不断地进行睡眠
        static class TimeWaiting implements Runnable {
            @Override
            public void run() {
                while (true) {
                    SleepUtils.second(100);
                }
            }
        }
        // 该线程在Waiting.class实例上等待
        static class Waiting implements Runnable {
            @Override
            public void run() {
                while (true) {
                    synchronized (Waiting.class) {
                        try {
                            Waiting.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        // 该线程在Blocked.class实例上加锁后，不会释放该锁
        static class Blocked implements Runnable {
            public void run() {
                synchronized (Blocked.class) {
                    while (true) {
                        SleepUtils.second(100);
                    }
                }
            }
        }
}
