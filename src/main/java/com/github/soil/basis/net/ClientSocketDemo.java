package com.github.soil.basis.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author tanruidong
 * @date 2020/12/09 11:15
 */
public class ClientSocketDemo {
    public static void main(String[] args) throws IOException {
        try(Socket socket = new Socket("www.baidu.com", 80);){
            socket.setSoTimeout(15000);
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = isr.read()) != -1){
                sb.append((char) i);
            }
            System.out.println(sb.toString());
        }
    }
}
