package site.shanzhao.soil.basis.reflect.proxy;

/**
 * @author tanruidong
 * @date 2020/08/26 23:09
 */
public class CloneDemo {

    public static void main(String[] args) {
        Class[] classArr = {ParamInterface.class, NoParamInterface.class};
        Class[] cloneClass = classArr.clone();
        for (Class aClass : cloneClass) {
            System.out.println(aClass.getName());
        }
        System.out.println("ParamInterface [==] result: "+(classArr[0] == cloneClass[0]));
        System.out.println("ParamInterface [equals()] result: "+(classArr[0].equals(cloneClass[0])));
        System.out.println(classArr[1] == cloneClass[1]);
        System.out.println(classArr[1].equals(cloneClass[1]));
    }
}
