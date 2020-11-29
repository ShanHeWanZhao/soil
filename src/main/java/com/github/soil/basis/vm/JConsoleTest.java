package com.github.soil.basis.vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author tanruidong
 * @date 2020/09/08 21:38
 */
public class JConsoleTest {
    /**
     * 内存占位符对象，一个OOMObject大约占64KB
     */
    static class OOMObject {
        public byte[] placeholder = new byte[64 * 1024];
    }
    public static void fillHeap(int num) throws InterruptedException {
        List<OOMObject> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            // 稍作延时，令监视曲线的变化更加明显
            Thread.sleep(50);
            System.out.println("add one");
            list.add(new OOMObject());
        }
        System.gc();
    }
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String next = scanner.next();
            if (next.equals("1")){
                fillHeap(1000);
                System.out.println("over");
            }
        }
    }

}
