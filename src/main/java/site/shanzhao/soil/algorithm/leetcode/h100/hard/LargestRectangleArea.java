package site.shanzhao.soil.algorithm.leetcode.h100.hard;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <a href="https://leetcode.cn/problems/largest-rectangle-in-histogram/?envType=study-plan-v2&envId=top-100-liked">
 *     84. 柱状图中的最大矩形</a>
 *
 * 整体思路：
 * 1. 使用单调栈（单调递增栈）来存储柱状图中柱子的索引。
 * 2. 遍历柱状图中的每一个柱子：
 *    - 如果当前柱子的高度小于栈顶索引对应的柱子高度，则说明找到了一个右边界，可以计算以栈顶柱子为高的最大矩形面积。
 *    - 弹出栈顶索引，计算宽度（当前索引与栈顶索引的差值减一），并更新最大面积。
 *    - 将当前柱子的索引入栈，保持栈的单调递增性质。
 * 3. 在遍历结束后，处理栈中剩余的柱子，计算以这些柱子为高的最大矩形面积。
 * 4. 最终返回最大矩形面积。
 *
 * 时间复杂度：O(n)，其中 n 是柱状图的柱子数量。每个柱子只会被压入和弹出栈一次。
 * 空间复杂度：O(n)，在最坏情况下，栈的大小可能达到柱子数量。
 */
public class LargestRectangleArea {

    public int largestRectangleArea(int[] heights) {
        // 单调递增栈，用于存储柱子的索引
        Deque<Integer> stack = new ArrayDeque<>();
        // 初始化最大面积为 0
        int max = 0;
        // 在栈中压入一个哨兵值 -1，用于处理边界情况
        stack.push(-1);

        // 遍历柱状图中的每一个柱子（包括一个虚拟的右边界）
        for (int i = 0; i <= heights.length; i++) {
            // 如果当前索引等于柱子数量，则高度为 0（虚拟右边界）
            int height = i == heights.length ? 0 : heights[i];

            // 如果当前高度小于栈顶索引对应的柱子高度，则计算以栈顶柱子为高的最大矩形面积
            while (stack.peek() != -1 && heights[stack.peek()] > height) {
                int preIndex = stack.pop(); // 弹出栈顶索引
                int width = i - stack.peek() - 1; // 计算宽度
                max = Math.max(max, width * heights[preIndex]); // 更新最大面积
            }

            // 将当前柱子的索引入栈
            stack.push(i);
        }

        // 返回最大矩形面积
        return max;
    }
}
