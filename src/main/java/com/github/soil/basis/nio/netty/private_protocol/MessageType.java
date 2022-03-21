package com.github.soil.basis.nio.netty.private_protocol;

/**
 * @author tanruidong
 * @date 2021/02/19 12:48
 */
public enum MessageType {
    LOGIN_REQ,
    LOGIN_RESP,
    HEARTBEAT_RESP,
    HEARTBEAT_REQ;

    public byte value(){
        return (byte) this.ordinal();
    }
}
