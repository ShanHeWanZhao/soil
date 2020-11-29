//package com.github.soil.basis.unsafe;
//
//import sun.misc.Unsafe;
//
//import java.lang.reflect.Field;
//
///**
// * @author tanruidong
// * @date 2020/08/19 18:52
// */
//public class UnsafeGet {
//    public static void main(String[] args) throws Exception {
//        Unsafe unsafe = reflectGetUnsafe();
//        unsafe.park(false, 0);
//        System.out.println(unsafe);
//    }
//
//    private static Unsafe reflectGetUnsafe() throws Exception {
//        Field unSafe = Unsafe.class.getDeclaredField("theUnsafe");
//        unSafe.setAccessible(true);
//        return (Unsafe) unSafe.get(null);
//    }
//}
