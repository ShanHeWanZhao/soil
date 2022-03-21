package com.github.soil.basis.nio.netty.private_protocol;

/**
 * @author tanruidong
 * @date 2021/02/19 12:51
 */
public enum ResultType {
    SUCCESS,
    FAIL;

    public byte value(){
        return (byte) this.ordinal();
    }
}
