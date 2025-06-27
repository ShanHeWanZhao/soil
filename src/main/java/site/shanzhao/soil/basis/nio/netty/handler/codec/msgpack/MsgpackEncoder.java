//package site.shanzhao.soil.basis.nio.netty.handler.codec.msgpack;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToByteEncoder;
//import org.msgpack.MessagePack;
//import org.msgpack.core.MessagePack;
//
///**
// * @author tanruidong
// * @date 2021/01/23 16:34
// */
//public class MsgpackEncoder extends MessageToByteEncoder<Object> {
//    @Override
//    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
//        MessagePack messagePack = new MessagePack();
//        // 序列化
//        org.msgpack.core.MessagePack.newDefaultBufferPacker().w
//        byte[] message = messagePack.write(msg);
//        out.writeBytes(message);
//    }
//}
