package com.github.soil.basis.extend;

import org.junit.Test;

/**
 * @author tanruidong
 * @date 2020/08/17 15:36
 */
public class PrivateTest {
    @Test
    public void test(){
        Son son = new Son();
        son.setFatherName("爸爸");
        System.out.println(son.getFatherName());
    }
}
