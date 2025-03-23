package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <a href="https://leetcode.cn/problems/min-stack/?envType=study-plan-v2&envId=top-100-liked">
 *     155. 最小栈</a>
 *
 * 整体思路：
 * 1. 使用两个栈：
 *    - `dataStack`：用于存储所有压入栈的元素。
 *    - `minStack`：用于存储当前栈中的最小值。
 * 2. 当压入一个新元素时：
 *    - 将其压入 `dataStack`。
 *    - 如果 `minStack` 为空，或者新元素小于等于 `minStack` 的栈顶元素，则将其也压入 `minStack`。（
 *          因为如果准备push的值a大于了minStack的栈顶元素b，那么在这个元素a pop出去之前，栈内最小的元素一定是元素b
 * 3. 当弹出栈顶元素时：
 *    - 从 `dataStack` 弹出栈顶元素。
 *    - 如果弹出的元素等于 `minStack` 的栈顶元素，则也从 `minStack` 弹出栈顶元素。
 * 4. 获取栈顶元素时，直接返回 `dataStack` 的栈顶元素。
 * 5. 获取当前栈中的最小值时，直接返回 `minStack` 的栈顶元素。
 *
 * 时间复杂度：
 * - `push`：O(1)，只需将元素压入栈中，并进行一次比较。
 * - `pop`：O(1)，只需从栈中弹出元素，并进行一次比较。
 * - `top`：O(1)，直接返回栈顶元素。
 * - `getMin`：O(1)，直接返回 `minStack` 的栈顶元素。
 *
 * 空间复杂度：O(n)，其中 n 是栈中元素的数量。需要额外的 `minStack` 来存储最小值。
 */
public class MinStack {

    // 用于存储所有元素的栈
    private final Deque<Integer> dataStack;
    // 用于存储当前最小值的栈
    private final Deque<Integer> minStack;

    public MinStack() {
        dataStack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }

    public void push(int val) {
        dataStack.push(val);
        // 如果 minStack 为空，或者当前元素小于等于 minStack 的栈顶元素，则将其压入 minStack
        if (minStack.isEmpty() || minStack.peek() >= val){
            minStack.push(val);
        }
    }

    public void pop() {
        // 如果弹出的元素等于 minStack 的栈顶元素，则也从 minStack 弹出栈顶元素
        if (dataStack.pop().equals(minStack.peek())){
            minStack.pop();
        }

    }

    public int top() {
        return dataStack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}
