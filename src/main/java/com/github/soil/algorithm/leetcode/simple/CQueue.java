package com.github.soil.algorithm.leetcode.simple;

import java.util.Stack;

/**
 * 剑指Offer09，两个栈实现队列
 * https://leetcode-cn.com/problems/yong-liang-ge-zhan-shi-xian-dui-lie-lcof/
 * @author tanruidong
 * @date 2022/04/18 16:31
 */
class CQueue {

    private Stack<Integer> stackIn = new Stack<>();
    private Stack<Integer> stackOut = new Stack<>();

    public CQueue() {
    }


    public void push(Integer num){
        stackIn.push(num);
    }
    public Integer pop(){
        while (!stackIn.isEmpty()){
            stackOut.push(stackIn.pop());
        }
        return stackOut.pop();
    }

    public int size(){
        return stackIn.size() + stackOut.size();
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
//        queue.appendTail(5);
//        queue.appendTail(4);
//        queue.appendTail(3);
//        queue.appendTail(2);
//        queue.appendTail(1);
//        System.out.println(queue.deleteHead());
//        System.out.println(queue.deleteHead());
//        System.out.println(queue.deleteHead());
//        System.out.println(queue.deleteHead());
//        System.out.println(queue.deleteHead());
//        System.out.println(queue.deleteHead());
        queue.push(1);
        queue.push(2);
        queue.push(3);
        queue.push(4);
        queue.push(5);
        while (queue.size() > 0){
            System.out.println(queue.pop());
        }
        System.out.println("=================");
        Stack<Integer> stacks = new Stack<>();
        stacks.push(1);
        stacks.push(2);
        stacks.push(3);
        stacks.push(4);
        while (!stacks.isEmpty()){
            System.out.println(stacks.pop());
        }
    }
}
