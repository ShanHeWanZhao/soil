package site.shanzhao.soil.basis.nio.netty.http.response;

import site.shanzhao.soil.basis.nio.netty.http.AbstractHttpJsonDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

import java.util.List;

/**
 * @author tanruidong
 * @date 2021/01/30 15:00
 */
public class HttpJsonResponseDecoder extends AbstractHttpJsonDecoder<DefaultFullHttpResponse> {

    public HttpJsonResponseDecoder(Class<?> targetClass) {
        super(targetClass);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> out) throws Exception {
        HttpJsonResponse response = new HttpJsonResponse(msg, decode0(msg.content()));
        out.add(response);
    }
}
