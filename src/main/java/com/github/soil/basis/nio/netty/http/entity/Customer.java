package com.github.soil.basis.nio.netty.http.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author tanruidong
 * @date 2021/01/30 12:35
 */
@Data
@Accessors(chain = true)
public class Customer {
    private long customerNumber;
    private String firstName;
    private String lastName;
    private List<String> middleName;
}
