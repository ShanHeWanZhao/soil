package site.shanzhao.soil.tomcat.classloader;

import com.alibaba.ttl.TransmittableThreadLocal;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LeakingServlet3 extends HttpServlet {

    public final TransmittableThreadLocal<MyCounter> COUNTER_THREAD_LOCAL = TransmittableThreadLocal.withInitial(MyCounter::new);

    public static class MyCounter {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        MyCounter counter = COUNTER_THREAD_LOCAL.get();
        counter.increment();
        // doBusiness...
    }
}