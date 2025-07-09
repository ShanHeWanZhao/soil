package site.shanzhao.soil.basis.nio.netty.original.nio;

/**
 * @author tanruidong
 * @date 2021/01/16 20:17
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
    }
}
