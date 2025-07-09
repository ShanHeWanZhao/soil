package site.shanzhao.soil.basis.thread;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tanruidong
 * @date 2020/08/04 13:47
 */
public class ThreadLocalTest {
    private static ThreadLocal<Map<String, String>> local = new ThreadLocal<>();
//    private static final Object lock = new Object();
    private static final Lock lock = new ReentrantLock();
    static {
        local.set(new HashMap<>());
    }
    @Test
    public void threadLocalTest() throws Exception {
        final Condition cond = lock.newCondition();
        int threadCount = 10;
        CountDownLatch threadUtil = new CountDownLatch(threadCount);
        Runnable threadLocalRun = () -> {
            local.set(new HashMap<>());
            Map<String, String> map = local.get();
            map.put("111", "222");
            try {
                cond.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            threadUtil.countDown();
        };
        for (int i = 1;i <= threadCount; i++){
            new Thread(threadLocalRun, "ThreadLocal "+i).start();
        }
        threadUtil.await();
    }

}
