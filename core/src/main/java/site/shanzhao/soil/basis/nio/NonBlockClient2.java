package site.shanzhao.soil.basis.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * @author tanruidong
 * @date 2020/06/21 18:44
 */
public class NonBlockClient2 {
    public static void main(String[] args) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 创建客户端的channel
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
        // 切换为非阻塞
        sChannel.configureBlocking(false);
        // 创建Buffer，准备写入数据
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 扫描器，实现持续写入
        Scanner scanner = new Scanner(System.in);
        // Scanner.hasNext()方法是阻塞的，只会返回true
        while (scanner.hasNext()){
            String next = scanner.nextLine();
            buffer.put(("【Client2: "+next+"】"+LocalDateTime.now().format(formatter)).getBytes());
            buffer.flip();
            // 将Buffer的数据写入到Channel中
            sChannel.write(buffer);
            buffer.clear();
        }
        sChannel.close();
    }
}
