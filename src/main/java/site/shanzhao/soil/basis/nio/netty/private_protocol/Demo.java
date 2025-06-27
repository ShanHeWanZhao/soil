package site.shanzhao.soil.basis.nio.netty.private_protocol;

import io.netty.util.concurrent.EventExecutor;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tanruidong
 * @date 2021/02/20 21:25
 */
public class Demo {
    private final AtomicInteger idx = new AtomicInteger();

    @Test
    public void test(){
        for (int i = 0;i < 50; i++){
            int num = idx.getAndIncrement();
            int a = num & 8 - 1;
            System.out.println(num + ": "+ a);
        }
    }

    @Test
    public void test_1(){
        int i = 3;
        System.out.println((i & -i) == i);

    }

    @Test
    public void test2(){
        int ops = 0;
        ops &= ~SelectionKey.OP_CONNECT;
        System.out.println(ops);
        System.out.println(Integer.toBinaryString(~SelectionKey.OP_CONNECT));
    }

    @Test
    public void test3(){
        System.out.println(Math.abs(1 % 3));
        System.out.println(Math.abs(2 % 3));
        System.out.println(Math.abs(3 % 3));
        System.out.println(Math.abs(4 % 3));
    }
}
