package site.shanzhao.soil.basis.nio.netty.original.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author tanruidong
 * @date 2021/01/17 00:10
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("start execute read，result is "+result);
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try{
            String req = new String(body, StandardCharsets.UTF_8);
            System.out.println("The AIO time server receive order : [" + req + "]");
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? LocalDateTime.now().toString() : "BAD ORDER";
            doWrite(currentTime);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void doWrite(String currentTime) {
        byte[] bytes = currentTime.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        System.out.println("服务端开始向SocketChannel写入数据，长度为："+writeBuffer.remaining());
        // 写数据到Channel中，此时ByteBuffer会变成被读模式
        channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                // 如果没有发送完成，继续发送
                if (attachment.hasRemaining()) {
                    channel.write(attachment, attachment, this);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
