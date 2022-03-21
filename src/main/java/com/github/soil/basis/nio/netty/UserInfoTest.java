package com.github.soil.basis.nio.netty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author tanruidong
 * @date 2021/01/23 16:08
 */
public class UserInfoTest {
    public static void main(String[] args) throws IOException {
        int loop = 1000000;
        int serLength = 0;
        int otherLength = 0;
        UserInfo info = UserInfo.builder()
                .username("tanruidong")
                .userId(100).build();
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        long serStart = System.currentTimeMillis();
        for (int i = 0;i < loop;i++){
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(info);
            os.flush();
            os.close();
            byte[] b = bos.toByteArray();
            if (serLength == 0){
                serLength = b.length;
            }
            bos.close();
        }
        String serFormat = String.format("The jdk serializable length is :[%s], loop [%s] times cost [%s]ms",
                serLength, loop, System.currentTimeMillis() - serStart);
        System.out.println(serFormat);
        System.out.println("---------------------------");
        long otherStart = System.currentTimeMillis();
        for (int i = 0;i < loop;i++){
            byte[] bytes = info.codeC();
            if (otherLength == 0){
                otherLength = bytes.length;
            }
        }
        String otherFormat = String.format("The byte array serializable length is :[%s], loop [%s] times cost [%s]ms",
                otherLength, loop, System.currentTimeMillis() - otherStart);
        System.out.println(otherFormat);
    }
}
