package com.github.soil.basis;

import com.github.soil.basis.nio.netty.UserInfo;
import com.sun.org.glassfish.external.probe.provider.annotations.ProbeParam;
import org.junit.Test;

import javax.jws.WebParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Test
    public void test1() throws NoSuchMethodException {
        Method a = this.getClass().getMethod("a", String.class, UserInfo.class, List.class,Map.class);
        Annotation[][] annotations = a.getParameterAnnotations();
        Class<?>[] parameterTypes = a.getParameterTypes();
        Type[] genericParameterTypes = a.getGenericParameterTypes();
        System.out.println(annotations);
    }

    public void a(@WebParam @ProbeParam("111") String b,UserInfo userInfo, List<String> list, Map<String,Integer> map){

    }
}
