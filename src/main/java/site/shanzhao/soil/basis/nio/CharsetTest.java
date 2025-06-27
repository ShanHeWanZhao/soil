package site.shanzhao.soil.basis.nio;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.Arrays;
import java.util.SortedMap;

/**
 * @author tanruidong
 * @date 2020/06/20 18:00
 */
public class CharsetTest {
    @Test
    public void test() {
        final Charset charset = Charset.defaultCharset();
        final SortedMap<String, Charset> map = Charset.availableCharsets();
        System.out.println(charset);
        for (String name :map.keySet() ){
            System.out.println(map.get(name));
        }
    }

    @Test
    public void test2() throws Exception {
        String name = "BM编码及";
        // 创建缓冲区，放入数据
        CharBuffer charBuffer = CharBuffer.allocate(20);
        charBuffer.put(name);
        // 翻转为被读模式
        charBuffer.flip();

        System.out.println("--------------GBK-----------------");
        Charset gbk = Charset.forName("GBK");
        CharsetEncoder gbkEncoder = gbk.newEncoder();
        CharsetDecoder gbkDecoder = gbk.newDecoder();
        ByteBuffer encode = gbkEncoder.encode(charBuffer);
        System.out.println("GBK字节数组："+Arrays.toString(encode.array()));
        System.out.println(gbkDecoder.decode(encode).toString());

        System.out.println("--------------UTF-8-----------------");
        Charset utf8 = StandardCharsets.UTF_8;
        CharsetEncoder utf8Encoder = utf8.newEncoder();
        CharsetDecoder utf8Decoder = utf8.newDecoder();
        charBuffer.rewind();
        ByteBuffer encode1 = utf8Encoder.encode(charBuffer);
        System.out.println("UTF-8字节数组："+Arrays.toString(encode1.array()));
        System.out.println(utf8Decoder.decode(encode1).toString());
    }
}
