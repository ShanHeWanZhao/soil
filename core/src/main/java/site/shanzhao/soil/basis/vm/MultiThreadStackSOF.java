package site.shanzhao.soil.basis.vm;

/**
 * 多线程导致栈溢出
 * 还是别测了，会死机的
 * @author tanruidong
 * @date 2020/08/26 20:49
 */
public class MultiThreadStackSOF {
    private static int count = 1;
    private void dontStop(){
        while (true){
            try{
                Thread.sleep(1000 * 20);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
    public void stackLeakByThread() {
        while (true){
            new Thread(() -> dontStop()).start();
            count++;
        }
    }

    public static void main(String[] args) {
        try{
            MultiThreadStackSOF oom = new MultiThreadStackSOF();
            oom.stackLeakByThread();
        }catch(Throwable e){
            System.out.println("thread count: "+count);
            throw e;
        }
    }
}
