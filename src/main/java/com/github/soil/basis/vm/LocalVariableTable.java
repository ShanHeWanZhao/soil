package com.github.soil.basis.vm;

/**
 * @author tanruidong
 * @date 2020/09/14 22:25
 */
public class LocalVariableTable {
    // 不会收集placeholder
//    public static void main(String[] args) {
//        byte[] placeholder = new byte[64 * 1024 * 1024];
//        System.gc();
//    }
    /*
        还是不会收集placeholder
        因为虽然placeholder已经不可达了，但是之后该栈帧的局部变量表并未发生任何的读写操作
        placeholder原本占用的变量槽还未被其它变量复用，所以GC Roots仍然有关联
     */
//    public static void main(String[] args) {
//        {
//            byte[] placeholder = new byte[64 * 1024 * 1024];
//        }
//        System.gc();
//    }
    // 收集了placeholder
    public static void main(String[] args) {
        {
            byte[] placeholder = new byte[64 * 1024 * 1024];
        }
        int a = 0;
        System.gc();
    }
}
