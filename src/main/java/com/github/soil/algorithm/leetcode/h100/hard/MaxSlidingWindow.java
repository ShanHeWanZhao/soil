package com.github.soil.algorithm.leetcode.h100.hard;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *<a href="https://leetcode.cn/problems/sliding-window-maximum/description/?envType=study-plan-v2&envId=top-100-liked">
 *     239.滑动窗口的最大值</a>
 *
 * 解法思路：
 * 使用单调队列（双端队列实现）维护当前窗口中的最大值索引，队列按元素值单调递减排列。
 * 遍历数组时，确保队列中的索引对应的元素始终满足单调递减性质，并移除超出窗口范围的索引。
 *
 * 时间复杂度：O(n)，每个元素最多入队和出队一次。
 * 空间复杂度：O(k)，队列最多存储 k 个元素。
 */
public class MaxSlidingWindow {
    public int[] maxSlidingWindow(int[] nums, int k) {
        // 单调递减队列，存储的是nums的索引，队列对应的元素值严格单调递减
        Deque<Integer> queue = new ArrayDeque<>();
        int[] result = new int[nums.length - k + 1];

        for (int i = 0; i < nums.length; i++) {

            // 维护单调递减性质：移除队列中所有小于当前元素的索引
            // 因为这些元素不可能是当前或后续窗口的最大值
            while (!queue.isEmpty() && nums[queue.peekLast()] <= nums[i]) {
                queue.pollLast();
            }
            queue.addLast(i);

            // 当窗口形成时（i >= k-1），可以记录结果了
            if (i >= k - 1) {
                // 当前窗口的最大值即为队列首部索引对应的元素
                result[i - k + 1] = nums[queue.peekFirst()];
                // 为下一个区间做准备
                // 检查队首索引是否已经超出当前窗口范围（左边界索引为 i-k+1）
                // 如果是，则移除该索引，因为它不会出现在下一个窗口中
                if (i - k + 1 == queue.peekFirst()) {
                    queue.pollFirst();
                }
            }

        }
        return result;
    }
}
