package site.shanzhao.soil.basis.vm;

/**
 * 单线程栈溢出测试
 * 参数：
 *  -Xss128K: 栈容量为128K
 * @author tanruidong
 * @date 2020/08/26 20:43
 */
public class JavaVMStackSOF {
    private int stackLength = 1;
    public void  stackLeak(){
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackSOF oom = new JavaVMStackSOF();
        try{
            oom.stackLeak();
        }catch(Throwable e){
            System.out.println("Stack length: " + oom.stackLength);
            throw e;
        }
    }
}
