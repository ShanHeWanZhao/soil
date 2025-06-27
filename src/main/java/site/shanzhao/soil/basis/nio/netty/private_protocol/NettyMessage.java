package site.shanzhao.soil.basis.nio.netty.private_protocol;

import lombok.Data;

/**
 * @author tanruidong
 * @date 2021/02/19 11:04
 */
@Data
public class NettyMessage {
    private Header header;
    private Object body;
}
