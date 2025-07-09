package site.shanzhao.soil.algorithm.leetcode.simple;

import java.util.Stack;

/**
 * 剑指Offer09，两个栈实现队列
 * https://leetcode-cn.com/problems/yong-liang-ge-zhan-shi-xian-dui-lie-lcof/
 * @author tanruidong
 * @date 2022/04/18 16:31
 */
class CQueue {

    private Stack<Integer> stackIn;
    private Stack<Integer> stackOut;

    public CQueue() {
        stackIn = new Stack<>();
        stackOut = new Stack<>();
    }

    public void appendTail(Integer value) {
         stackIn.push(value);
    }

    public Integer deleteHead() {
        if (!stackOut.isEmpty()){
            return stackOut.pop();
        }else {
            if (!stackIn.isEmpty()){
                int size = stackIn.size();
                for (int i = 0; i < size; i++) {
                    stackOut.push(stackIn.pop());
                }
                return stackOut.pop();
            }else {
                return -1;
            }
        }
    }

    public static void main(String[] args) {
        CQueue queue = new CQueue();
        queue.appendTail(5);
        queue.appendTail(4);
        queue.appendTail(3);
        queue.appendTail(2);
        queue.appendTail(1);
        System.out.println(queue.deleteHead());
        System.out.println(queue.deleteHead());
        System.out.println(queue.deleteHead());
        System.out.println(queue.deleteHead());
        System.out.println(queue.deleteHead());
        System.out.println(queue.deleteHead());
    }
}
