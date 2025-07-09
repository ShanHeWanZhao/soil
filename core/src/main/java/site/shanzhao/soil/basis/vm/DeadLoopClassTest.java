package site.shanzhao.soil.basis.vm;

/**
 * @author tanruidong
 * @date 2020/09/14 21:33
 */
public class DeadLoopClassTest {
    public static void main(String[] args) {
        Runnable script = new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread() + "start");
                DeadLoopClass dlc = new DeadLoopClass();
                System.out.println(Thread.currentThread() + " run over");
            }
        };
        Thread thread1 = new Thread(script, "DeadLoopThread 1");
        Thread thread2 = new Thread(script, "DeadLoopThread 2");
        thread1.start();
        thread2.start();
    }


    static class DeadLoopClass{
        static {
            if (true){
                System.out.println(Thread.currentThread() +" init DeadLoopClass");
                while (true){

                }
            }
        }
    }
}
