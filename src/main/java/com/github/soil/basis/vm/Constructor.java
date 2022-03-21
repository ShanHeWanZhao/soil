package com.github.soil.basis.vm;

/**
 * @author tanruidong
 * @date 2020/08/18 16:06
 */
public class Constructor {
    static Constructor c = new Constructor();

    public static void main(String[] args) {
        Constructor constructor = new Constructor();
        System.out.println(constructor.getClass());
//        System.out.println(c.getClass());
    }
}
