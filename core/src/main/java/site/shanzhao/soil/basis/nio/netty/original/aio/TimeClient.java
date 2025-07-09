package site.shanzhao.soil.basis.nio.netty.original.aio;

/**
 * @author tanruidong
 * @date 2021/01/18 19:53
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8888;
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001").start();
    }
}
