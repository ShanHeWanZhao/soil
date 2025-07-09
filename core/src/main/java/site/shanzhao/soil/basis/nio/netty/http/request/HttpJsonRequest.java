package site.shanzhao.soil.basis.nio.netty.http.request;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author tanruidong
 * @date 2021/01/30 12:42
 */
@Data
@AllArgsConstructor
public class HttpJsonRequest {
    private FullHttpRequest request;
    private Object body;
}
