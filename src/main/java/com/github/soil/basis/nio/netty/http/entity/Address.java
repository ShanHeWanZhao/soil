package com.github.soil.basis.nio.netty.http.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author tanruidong
 * @date 2021/01/30 12:39
 */
@Data
@Accessors(chain = true)
public class Address {
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String postCode;
    private String country;
}
