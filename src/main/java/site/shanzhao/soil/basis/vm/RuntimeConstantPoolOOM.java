package site.shanzhao.soil.basis.vm;

import java.util.HashSet;
import java.util.Set;

/**
 * 常量池溢出测试
 * jdk1.8后常量池放在堆中，所以前两个参数无效，且还会提示
 * ignoring option MaxPermSize=6M; support was removed in 8.0
 * ignoring option PermSize=6M; support was removed in 8.0
 * 参数：
 *  -XX:MaxPermSize=6M
 *  -XX:PermSize=6M
 *  -Xms6M ：堆初始大小为6M
 *  -Xmx6M ：堆最大大小为6M
 * @author tanruidong
 * @date 2020/08/26 21:36
 */
public class RuntimeConstantPoolOOM {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        int i = 0;
        try{
            while (true){
                set.add(String.valueOf(i++).intern());
            }
        }catch(Throwable e){
            System.out.println("string count:"+i);
            throw e;
        }
    }
}
