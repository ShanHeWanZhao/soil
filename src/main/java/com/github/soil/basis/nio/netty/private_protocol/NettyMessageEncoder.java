package com.github.soil.basis.nio.netty.private_protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 消息编码后的格式：
 * crcCode(4) + length(4，最后会重新计算) + sessionID(8) + type (1) + priority(1) + attachmentCount(4) +
 * [key的bytes大小(4) + key的bytes(n) + value的bytes大小(4) + value的bytes(n)]附件循环 +
 * body的bytes大小(4) + body的bytes
 * @author tanruidong
 * @date 2021/02/19 11:07
 */
@Slf4j
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {
    private final NettyMarshallingEncoder  marshallingEncoder =  MarshallingCodeCFactory.buildMarshallingEncoder();

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
        if (msg == null || msg.getHeader() == null){
            throw new IllegalArgumentException("the encode message is null");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        sendBuf.writeInt(msg.getHeader().getLength());
        sendBuf.writeLong(msg.getHeader().getSessionID());
        sendBuf.writeByte(msg.getHeader().getType());
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());
        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes(StandardCharsets.UTF_8);
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(ctx, value, sendBuf);
        }
        if (msg.getBody() != null){
            marshallingEncoder.encode(ctx,msg.getBody(), sendBuf);
        }else { // 没有数据,进行补位,为了方便后续的 decoder操作
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(4, sendBuf.readableBytes());
        // 把Message添加到List传递到下一个Handler
        out.add(sendBuf);
    }
}
