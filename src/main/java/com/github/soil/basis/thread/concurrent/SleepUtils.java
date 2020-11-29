package com.github.soil.basis.thread.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * @author tanruidong
 * @date 2020/07/30 11:13
 */
public class SleepUtils {
    public static final void second(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
        }
    }
}
