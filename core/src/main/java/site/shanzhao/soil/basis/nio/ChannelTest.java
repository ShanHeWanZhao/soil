package site.shanzhao.soil.basis.nio;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

/**
 * @author tanruidong
 * @date 2020/06/20 14:38
 */
public class ChannelTest {

    private String dir = System.getProperty("user.dir") + "/src/main/resources/img/";

    /**
     * 非直接缓冲区 350M左右的文件耗时4.5s左右
     */
    @Test
    public void test1() {
        final long start = System.currentTimeMillis();
        try (
                FileInputStream fis = new FileInputStream("E:/迅雷下载/mysql-5.7.29-win32.zip");
                FileOutputStream fos = new FileOutputStream("E:/迅雷下载/mysql-5.7.29(1)-win32.zip");
                // 获取通道
                final FileChannel fisChannel = fis.getChannel();
                final FileChannel fosChannel = fos.getChannel()
        ) {

            // 分配缓冲区
            final ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 读+写
            while ((fisChannel.read(buffer)) > 0) {
                buffer.flip();
                fosChannel.write(buffer);
                buffer.clear();
            }
            final long end = System.currentTimeMillis();
            System.out.println("耗时(秒)：" + (end - start) / 1000.0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 直接缓冲区（大文件更快） 350M左右的文件耗时0.4s左右
     */
    @Test
    public void test2() {
        final long start = System.currentTimeMillis();
        try (
                // 获取通道
                FileChannel readChannel = FileChannel
                        .open(Paths.get("E:/迅雷下载/mysql-5.7.29-win32.zip")
                                , StandardOpenOption.READ);
                final FileChannel writeChannel = FileChannel
                        .open(Paths.get("E:/迅雷下载/mysql-5.7.29(2)-win32.zip"),
                                StandardOpenOption.READ
                                , StandardOpenOption.WRITE
                                , StandardOpenOption.CREATE);
        ) {
            // 获取直接缓冲区
            MappedByteBuffer readMap = readChannel.map(FileChannel.MapMode.READ_ONLY, 0, readChannel.size());
            MappedByteBuffer writeMap = writeChannel.map(FileChannel.MapMode.READ_WRITE, 0, readChannel.size());
            // 读取+写入
            byte[] bytes = new byte[readMap.limit()];
            readMap.get(bytes);
            writeMap.put(bytes);
            final long end = System.currentTimeMillis();
            System.out.println("直接内存耗时（秒）：" + (end - start) / 1000.0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 通道之间传输数据（直接缓冲区）
     * transferTo
     * transferFrom
     */
    @Test
    public void test3() {
        final long start = System.currentTimeMillis();
        try (
                // 获取通道
                FileChannel readChannel = FileChannel
                        .open(Paths.get("E:/迅雷下载/mysql-5.7.29-win32.zip")
                                , StandardOpenOption.READ);
                final FileChannel writeChannel = FileChannel
                        .open(Paths.get("E:/迅雷下载/mysql-5.7.29(2)-win32.zip"),
                                StandardOpenOption.READ
                                , StandardOpenOption.WRITE
                                , StandardOpenOption.CREATE);
        ) {
//            readChannel.transferTo(0, readChannel.size(), writeChannel);
            writeChannel.transferFrom(readChannel, 0, readChannel.size());
            final long end = System.currentTimeMillis();
            System.out.println("直接内存耗时（秒）：" + (end - start) / 1000.0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 分散读取：将通道的数据按顺序写入多个缓冲区（缓冲区数组）
     * 聚集写入：将多个缓冲区（缓冲区数组）按顺序聚集到通道中
     */
    @Test
    public void test4() {
        try (
                RandomAccessFile file = new RandomAccessFile("1.txt", "rw");
                FileChannel channel = file.getChannel();
        ) {
            // 分散读取
            ByteBuffer buffer1 = ByteBuffer.allocate(12);
            ByteBuffer buffer2 = ByteBuffer.allocate(1024);
            ByteBuffer[] buffers = {buffer1, buffer2};
            channel.read(buffers);
            for (ByteBuffer buffer: buffers) {
                buffer.flip();
                System.out.println(buffer.toString());
                System.out.println(new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8));
                System.out.println("-------------------------------");
            }
            byte[] array = buffers[0].array();
            byte[] array1 = buffers[1].array();
            System.out.println(Arrays.toString(array));
            System.out.println("size: "+array1.length+Arrays.toString(array1));
            // 聚集写入

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test5(){
        final Properties properties = System.getProperties();
        for (Object name : properties.keySet()){
            System.out.println(name+"="+properties.get(name));
        }
    }
}
