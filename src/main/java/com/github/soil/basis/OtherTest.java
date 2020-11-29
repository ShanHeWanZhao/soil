package com.github.soil.basis;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author tanruidong
 * @date 2020/09/01 10:49
 */
public class OtherTest {
    @Test
    public void test() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date dt = new Date();
//        System.out.println(format.format(dt));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        Date dt = c.getTime();
        System.out.println(format.format(dt));
    }
}
