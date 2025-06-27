package site.shanzhao.soil.basis.nio.netty.proto;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Arrays;

/**
 * @author tanruidong
 * @date 2021/01/24 16:20
 */
public class SubscribeReqProtoTest {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("Before encode:\n"+req.toString() );
        byte[] body = encode(req);
        System.out.println("编码后大小：["+body.length+"], 数据：["+Arrays.toString(body)+"]");
        SubscribeReqProto.SubscribeReq req2 = decode(body);
        System.out.println("After decode:\n"+req.toString() );
        System.out.println("Assert equal : --> "+req2.equals(req) );
    }
    public static byte[] encode(SubscribeReqProto.SubscribeReq req){
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    public static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder
                .setSubReqId(1)
                .setUsername("tanruidong") // 10
                .setProductName("Netty Book") // 10
                .addAllAddress(Arrays.asList("SiChuan ChengDu", "ShenZhen HongShuLin","Wuhan")); // 13 + 19
        return builder.build();
    }
}
