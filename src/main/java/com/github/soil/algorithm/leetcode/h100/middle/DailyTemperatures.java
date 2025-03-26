package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <a href="https://leetcode.cn/problems/daily-temperatures/description/?envType=study-plan-v2&envId=top-100-liked">
 *     739. 每日温度</a>
 *
 * 整体思路：
 * 1. 使用一个单调栈（单调递减栈）来存储温度的索引。
 * 2. 遍历温度数组中的每一个温度：
 *    - 如果当前温度大于栈顶索引对应的温度，则说明找到了一个更高的温度，计算天数差并更新结果数组。
 *    - 将当前温度的索引入栈，保持栈的单调递减性质。
 * 3. 最终返回结果数组，表示每一天需要等待多少天才能遇到更高的温度。
 *
 * 时间复杂度：O(n)，其中 n 是温度数组的长度。每个温度只会被压入和弹出栈一次。
 * 空间复杂度：O(n)，在最坏情况下，栈的大小可能达到数组长度。
 */
public class DailyTemperatures {

    public int[] dailyTemperatures(int[] temperatures) {
        // 结果数组，用于存储每一天需要等待的天数
        int[] result = new int[temperatures.length];
        // 单调递减栈，用于存储温度的索引
        Deque<Integer> decreasingStack = new ArrayDeque<>();

        // 遍历温度数组
        for (int i = 0; i < temperatures.length; i++) {
            // 如果当前温度大于栈顶索引对应的温度，则更新结果数组
            while (!decreasingStack.isEmpty() && temperatures[decreasingStack.peek()] < temperatures[i]) {
                int day = decreasingStack.pop(); // 弹出栈顶索引
                result[day] = i - day; // 计算天数差并更新结果
            }
            // 将当前温度的索引入栈
            decreasingStack.push(i);
        }

        // 返回结果数组
        return result;
    }
}
