package com.github.soil.nio;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author tanruidong
 * @date 2020/06/20 14:11
 */
public class ChannelTest {

    public static void main(String[] args) {
        try (
                FileInputStream fis = new FileInputStream("1.jpg");
                final FileOutputStream fos = new FileOutputStream("2.jpg");
                final FileChannel fisChannel = fis.getChannel();
                final FileChannel fosChannel = fos.getChannel()
        ) {

            final ByteBuffer buffer = ByteBuffer.allocate(1024);
            while ((fisChannel.read(buffer)) > 0) {
                buffer.flip();
                fosChannel.write(buffer);
                buffer.clear();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
