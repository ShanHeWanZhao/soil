package site.shanzhao.soil.basis.nio.netty.http;

import site.shanzhao.soil.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;

/**
 * @author tanruidong
 * @date 2021/01/30 13:34
 */
public abstract class AbstractHttpJsonDecoder<T> extends MessageToMessageDecoder<T> {

    private Class<?> targetClass;

    public AbstractHttpJsonDecoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    protected Object decode0(ByteBuf body){
        int length = body.readableBytes();
        byte[] array = new byte[length];
        body.getBytes(body.readerIndex(), array);
        String s = new String(array, StandardCharsets.UTF_8);
        System.out.println("The body is : "+s);
        return JsonUtils.string2Object(s, targetClass);
    }
}
