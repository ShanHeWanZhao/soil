package com.github.soil.basis.nio.netty.http.response;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author tanruidong
 * @date 2021/01/30 13:33
 */
@Data
@AllArgsConstructor
public class HttpJsonResponse {
    private FullHttpResponse response;
    private Object result;
}
