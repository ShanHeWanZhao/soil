package com.github.soil.basis.vm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 堆溢出测试参数：
 * -Xms20M ：初始堆内存
 * -Xmx20M：最大堆内存
 * -XX:+HeapDumpOnOutOfMemoryError：堆内存溢出错误进行dump
 * @author tanruidong
 * @date 2020/08/11 10:24
 */
public class HeapOOM {
    private long aa = 1L;
    private long bb = 1L;
    private long cc = 1L;
    /*
   出现堆溢出：
    java.lang.OutOfMemoryError: Java heap space
    Dumping heap to java_pid18516.hprof ...
    Heap dump file created [26775999 bytes in 0.191 secs]
    Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at com.github.soil.basis.vm.HeapOOM.main(HeapOOM.java:21)
     */
    public static void main(String[] args) {
        List<HeapOOM> oomList = new ArrayList<>();
        while (true){
            oomList.add(new HeapOOM());
        }
    }
}
