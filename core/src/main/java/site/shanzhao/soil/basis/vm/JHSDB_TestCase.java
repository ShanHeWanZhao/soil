package site.shanzhao.soil.basis.vm;

/**
 * vm参数：-Xmx10m -XX:+UseSerialGC -XX:-UseCompressedOops
 * 执行命令：jhsdb hsdb --pid 进程id（这种模式对SerialGC更友好，所以使用它）
 * jdk1.8不直接支持该命令，我使用jdk11，且在该版本包的bin目录下执行上面的命令
 *
 * 该图形化工具会用到的：
 * 1、Tool -> Heap Parameters:查看堆空间（各区域的起始和结束位置等）
 * 2、Windows -> Console(命令行模式，通过输入命令查看对应的内容)
 *   (1) universe:查看堆空间（和Heap Parameters一样）
 *   (1) scanoops [起始地址] [结束地址] [对象类的全名，例如：java/lang/Class]：查看该范围内指定的对象地址
 *   (2) revptrs [地址]：查看该地址（指针）在哪用到
 * 3、Tool -> Inspector: 输入地址后，可查看该地址代表的对象的详细信息
 * 4、Java Thread -> stack memory: 相当于查看栈上面的局部变量表（可用来查看栈上分配的指针）
 *
 * @author tanruidong
 * @date 2020/09/08 09:47
 */
public class JHSDB_TestCase {

    public static void main(String[] args) {
        int i = 1;
        Integer instanceI = 13231;
        Test test = new JHSDB_TestCase.Test();
        test.foo();
    }

    /*
        所有对象都在堆上存在，包括Class对象
     */
    static class Test {
        // staticObj在JHSDB_TestCase$Test.class对象内存储
        static ObjectHolder staticObj = new ObjectHolder();
        // instanceObj在堆上的JHSDB_TestCase$Test（$是内部类标志）对象上存储
        ObjectHolder instanceObj = new ObjectHolder();
        void foo() {
            int c = 100;
            // localObj在线程上的栈存储
            ObjectHolder localObj = new ObjectHolder();
//            try{
//                TimeUnit.SECONDS.sleep(1000);
//            }catch(Exception e){
//                System.out.println(e);
//            }
            System.out.println("done"); // 这里设一个断点
        }
    }
    private static class ObjectHolder {
        Integer instanceI = 1000;
        byte b = (byte) 1;
        int i = 10;
        double d = 3.3;
        boolean bo;
    }
}

