package site.shanzhao.soil.tomcat.classloader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LeakingServlet2 extends HttpServlet {

    public final ThreadLocal<MyCounter> COUNTER_THREAD_LOCAL = ThreadLocal.withInitial(MyCounter::new);

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