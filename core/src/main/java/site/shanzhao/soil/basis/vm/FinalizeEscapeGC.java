package site.shanzhao.soil.basis.vm;

/**
 * @author tanruidong
 * @date 2020/08/11 10:52
 */

/**
 * 此代码演示了两点：
 * 1.对象可以在被GC时自我拯救。
 * 2.这种自救的机会只有一次，因为一个对象的finalize()方法最多只会被系统自动调用一次
 *
 * @author zzm
 */
public class FinalizeEscapeGC {
    public static FinalizeEscapeGC SAVE_HOOK = null;

    public void isAlive() {
        System.out.println("[yes,i am still alive]");
    }

    /**
     * finalize方法在垃圾回收时会调用一次（只调用一次）
     * 注意：
     * finalize方法并不是一定等其执行完了才开始回收，因为待回收的对象会放在一个F-Queue队列中,
     * 为了避免它执行缓慢影响其他对象的回收，所以不一定会等它执行完才开始回收
     *
     *
     * 因此，下面的测试中，第一次gc不会回收，因为在finalize()方法在将其重新指向了一个对象引用（强引用）
     * 第二次gc会回收，因为finalize()方法不会再执行第二次了
     *
     * 所以：finalize()方法可以算对象"拯救自己"的方法
     * 但此方法开销高，不确定性大，无法保证各个对象的调用顺序，所以最好忘掉它，不使用它。
     * 该方法出现的原因仅仅是对c/c++转来的程序员的妥协而已，便于让这类人更接受java
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("[finalize method executed！]");
        FinalizeEscapeGC.SAVE_HOOK = this;
    }

    public static void main(String[] args) throws Throwable {
        SAVE_HOOK = new FinalizeEscapeGC();
        // 对象第一次成功拯救自己
        SAVE_HOOK = null;
        System.gc();
        // 因为finalize方法优先级很低，所以暂停0.5秒以等待它
        Thread.sleep(500);
        if (SAVE_HOOK != null){
            SAVE_HOOK.isAlive();
        }else{
            System.out.println("[no,i am dead]");
        }
        // 下面这段代码与上面的完全相同，但是这次自救却失败了
        SAVE_HOOK = null;
        System.gc();
        // 因为finalize方法优先级很低，所以暂停0.5秒以等待它
        Thread.sleep(500);
        if (SAVE_HOOK != null){
            SAVE_HOOK.isAlive();
        }else{
            System.out.println("[no,i am dead]");
        }
    }
}
