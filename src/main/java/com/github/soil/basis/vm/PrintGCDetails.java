package com.github.soil.basis.vm;

/**
 * @author tanruidong
 * @date 2020/08/11 19:44
 */
public class PrintGCDetails {
//    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        testAllocation();
    }
    /**
     *VM参数说明：
     -Xms20M    初始的Heap的大小
     -Xmx20M    最大的Heap的大小
     -Xmn10M    heap区的新生代为10M
     -XX:SurvivorRatio=8    两个survivor和eden区的大小比例，这里时至eden区占80%
     -XX:+PrintGCDetails    在发生垃圾收集行为时打印内存回收日志，
                                并且在进程退出的时候输出当前的内存各区域分配情况
     */
    public static void testAllocation(){
        int _1MB = 1024 * 1024;
        byte[]allocation1,allocation2,allocation3,allocation4;
        allocation1=new byte[2 * _1MB];
        allocation2=new byte[2*_1MB];
        allocation3=new byte[2*_1MB];
        allocation4=new byte[4*_1MB];//出现一次Minor GC
    }
}
