package site.shanzhao.soil.basis.nio.netty.private_protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanruidong
 * @date 2021/02/19 11:52
 */
@Slf4j
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final NettyMarshallingDecoder marshallingDecoder  = MarshallingCodeCFactory.buildMarshallingDecoder();

    /**
     * 那减8应该是因为要把CRC和长度本身占的减掉了。
     * @param maxFrameLength 第一个参数代表最大的序列化长度   1024*1024*5
     * @param lengthFieldOffset 代表长度属性的偏移量 简单来说就是message中 总长度的起始位置（Header中的length属性的起始位置）   本例中为4
     * @param lengthFieldLength 代表长度属性的长度 整个属性占多长（length属性为int，占4个字节）  4
     * @throws IOException
     */
    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,int lengthAdjustment){
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,lengthAdjustment,0);
    }



    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame  = (ByteBuf)super.decode(ctx, in);
        if(frame == null){
            return null;
        }

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());        //crcCode ----> 添加通信标记认证逻辑
        header.setLength(frame.readInt());        //length
        header.setSessionID(frame.readLong());    //sessionID
        header.setType(frame.readByte());        //type
        header.setPriority(frame.readByte());    //priority

        int size = frame.readInt();
        //附件个数大于0，则需要解码操作
        if (size > 0) {
            Map<String, Object> attch = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                frame.readBytes(keyArray);
                key = new String(keyArray, StandardCharsets.UTF_8);
                attch.put(key, marshallingDecoder.decode(ctx, frame));
            }
            //解码完成放入attachment
            header.setAttachment(attch);
        }
        // 对于ByteBuf来说，读一个数据，就会少一个数据，所以读完header，剩下的应该就是body了
        if(frame.readableBytes() > 4) { // 大于4个字节，肯定就有数据了（4个字节是内容长度的占位）
            message.setBody(marshallingDecoder.decode(ctx, frame));
        }

        message.setHeader(header);
        return message;
    }
}
