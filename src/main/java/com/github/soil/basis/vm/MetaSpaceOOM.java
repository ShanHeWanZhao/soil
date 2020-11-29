package com.github.soil.basis.vm;

import java.lang.reflect.Proxy;

/**
 * @author tanruidong
 * @date 2020/08/26 22:16
 */
public class MetaSpaceOOM {

    public static void main(String[] args) {
        while (true){
            OOMProxy proxyObject = (OOMProxy)Proxy.newProxyInstance(
                    MetaSpaceOOM.class.getClassLoader(),
                    new Class[]{OOMProxy.class},
                    (proxy, method, args1) -> {
                        if (method.getName().equals("proxyMethod")){
                            System.out.println("proxyMethod is invoked");
                        }
                        if (method.getName().equals("doNothing")){
                            System.out.println("doNothing is invoked");
                        }
                        return null;
                    });
            proxyObject.proxyMethod();
        }
    }
    public static interface OOMProxy{
        void proxyMethod();
        void doNothing();
    }
}
