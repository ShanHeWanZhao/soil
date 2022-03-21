package com.github.soil.basis.nio.netty.private_protocol;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanruidong
 * @date 2021/02/19 11:04
 */
@Data
public class Header {
    private int crcCode = 0Xabef0101;
    private int length;
    private long sessionID;
    private byte type;
    private byte priority;
    private Map<String, Object> attachment = new HashMap<>();
}
