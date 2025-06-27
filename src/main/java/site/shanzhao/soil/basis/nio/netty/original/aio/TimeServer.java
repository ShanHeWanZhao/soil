package site.shanzhao.soil.basis.nio.netty.original.aio;

/**
 * @author tanruidong
 * @date 2021/01/16 23:30
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8888;
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "AIO-AsyncTimeServerHandler-001").start();
    }
}
