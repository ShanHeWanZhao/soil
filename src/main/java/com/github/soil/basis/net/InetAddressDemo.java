package com.github.soil.basis.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author tanruidong
 * @date 2020/12/07 17:11
 */
public class InetAddressDemo {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress local = InetAddress.getLocalHost();
        System.out.println(local.getHostAddress());
    }
}
