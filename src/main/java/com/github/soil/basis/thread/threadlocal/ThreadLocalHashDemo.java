package com.github.soil.basis.thread.threadlocal;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tanruidong
 * @date 2020/11/21 10:54
 */
public class ThreadLocalHashDemo {
    private final int threadLocalHashCode = nextHashCode();
    private static AtomicInteger nextHashCode = new AtomicInteger();
    private static final int HASH_INCREMENT = 0x61c88647;
    private static final int INIT_CAP = 16;
    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    public static void main(String[] args) {
        calculate(20);
    }

    private static void calculate(int resizeCount){
        for (int j = 0; j < resizeCount; j++){
            int length =  INIT_CAP << j;
            HashMap<Integer, Integer> map = new HashMap<>();
            for(int i = 0;i < length;i++){
                int index =  new ThreadLocalHashDemo().threadLocalHashCode & (length - 1);
                if (map.containsKey(index)){
                    int oldValue = map.get(index);
                    map.replace(index, oldValue + 1);
                }else {
                    map.put(index , 1);
                }
            }
            Integer repeatCount = map.values().stream().filter(i -> i > 1).reduce(Integer::sum).orElse(0);
            String result = String.format("第[%s]次扩容后的重复率为：[%s]，数组长度为[%s]", j,
                    repeatCount / (double) map.size(), length);
            System.out.println(result);
        }
    }
}
