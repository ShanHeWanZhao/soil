package com.github.soil.basis.vm;

/**
 * @author tanruidong
 * @date 2020/08/27 17:04
 */
public class MaxTenuringThresholdTest {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        testTenuringThreshold();
    }
    /**
     * -XX：PretenureSizeThreshold参数只对Serial和ParNew两款新生代收集器有效
     * VM参数：-verbose:gc
     * -Xms20M
     * -Xmx20M
     * -Xmn10M
     * -XX:+PrintGCDetails
     * -XX:SurvivorRatio=8
     * -XX:MaxTenuringThreshold=1
     * -XX:+PrintTenuringDistribution
     *
     * -XX:+UseSerialGC
     * -Dfile.encoding=GBK
     */
    public static void testTenuringThreshold() {
        byte[] allocation1, allocation2, allocation3;
        // 什么时候进入老年代决定于XX:MaxTenuringThreshold设置
        allocation1 = new byte[_1MB / 4];
        allocation2 = new byte[4 * _1MB];
        allocation3 = new byte[4 * _1MB];
        allocation3 = null;
        allocation3 = new byte[4 * _1MB];
    }

}
