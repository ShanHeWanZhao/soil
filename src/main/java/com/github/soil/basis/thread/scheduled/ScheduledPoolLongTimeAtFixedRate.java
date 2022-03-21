package com.github.soil.basis.thread.scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 测试在单线程且固定的频率下，任务执行时间大于执行间隔对其他任务造成延后的影响
 * @author tanruidong
 * @date 2021/12/13 18:34
 */
public class ScheduledPoolLongTimeAtFixedRate {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> {
            try {
                System.out.println(formatter.format(LocalDateTime.now())+": sleep任务执行了");
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0,1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(() -> {
            System.out.println(formatter.format(LocalDateTime.now())+": 非sleep任务执行了");
        }, 5,2, TimeUnit.SECONDS);
    }
}
