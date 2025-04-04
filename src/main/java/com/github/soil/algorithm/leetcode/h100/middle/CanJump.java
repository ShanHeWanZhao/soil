package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/jump-game/?envType=study-plan-v2&envId=top-100-liked">
 *     55. 跳跃游戏</a>
 *
 * 思路：
 * 1. 使用贪心算法，维护一个变量 maxIndex，表示当前能够到达的最远位置。
 * 2. 遍历数组，对于每个位置 i，检查是否能够到达该位置（即 maxIndex >= i）。
 *    - 如果不能到达，说明无法继续跳跃，返回 false。
 *    - 如果能到达，更新 maxIndex 为当前位置 i 加上当前跳跃长度 nums[i]。
 * 3. 如果在遍历过程中 maxIndex 已经大于等于数组的最后一个位置，说明可以到达终点，返回 true。
 * 4. 遍历结束后，如果能够到达终点，返回 true。
 *
 * 时间复杂度：O(n)，其中 n 是数组 nums 的长度。只需要遍历一次数组。
 * 空间复杂度：O(1)，只使用了常数级别的额外空间。
 */
public class CanJump {
    public boolean canJump(int[] nums) {
        int maxIndex = 0;
        for (int i = 0; i < nums.length; i++) {
            if (maxIndex < i) { // 如果当前位置 i 超过了 maxIndex，说明无法到达当前位置，返回 false
                return false;
            }
            // 更新最远可到达的索引位置
            maxIndex = Math.max(maxIndex, i + nums[i]);
            if (maxIndex >= nums.length - 1) { // 如果 maxIndex 已经大于等于数组的最后一个位置，说明可以到达终点，返回 true
                return true;
            }
        }
        return true;
    }
}
