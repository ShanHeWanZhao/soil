//package site.shanzhao.soil.basis.nio.netty.handler.codec.msgpack;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToMessageDecoder;
//import org.msgpack.MessagePack;
//
//import java.util.List;
//
///**
// * @author tanruidong
// * @date 2021/01/23 16:38
// */
//public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
//        int length = msg.readableBytes();
//        byte[] array = new byte[length];
//        msg.getBytes(msg.readerIndex(), array, 0, length);
//        MessagePack msgpack = new MessagePack();
//        out.add(msgpack.read(array));
//    }
//}
