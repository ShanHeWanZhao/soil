package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/jump-game/?envType=study-plan-v2&envId=top-100-liked">
 *     45. 跳跃游戏2 </a>
    要求总结：跳跃到数组末尾，最少需要的跳跃次数（一定可以跳跃到数组末尾）
 *
 * 思路：
 * 1. 使用贪心算法，维护两个变量：
 *    - countIndex：表示当前跳跃能够到达的最远位置，当到达这个位置时，可以计数了就。
 *    - maxIndex：当前范围内能到的最远位置。
 * 2. 遍历数组，对于每个位置 i，更新 maxIndex 为当前位置 i 加上当前跳跃长度 nums[i]。
 * 3. 当遍历到 countIndex 时，表示当前跳跃已经结束，需要进行下一次跳跃：
 *    - 更新 countIndex 为 maxIndex。
 *    - 增加跳跃次数 result。
 * 4. 遍历结束后，最后一次跳跃需要额外增加一次跳跃次数。
 *
 * 时间复杂度：O(n)，其中 n 是数组 nums 的长度。只需要遍历一次数组。
 * 空间复杂度：O(1)，只使用了常数级别的额外空间。
 */
public class Jump {
    public int jump(int[] nums) {
        if (nums.length == 1){
            return 0;
        }
        int result = 0;
        // 计数位置
        int countIndex = nums[0];
        // 遍历过程中最远能到达的位置
        int maxIndex = 0;
        // 重点：只遍历到倒数第二个位置，避免最后一个位置的特殊处理
        for (int i = 0; i < nums.length - 1; i++) {
            // 更新下一步能够到达的最远位置
            maxIndex = Math.max(maxIndex, i + nums[i]);

            // 表示已经跳跃到这一步的最大位置了，可以更新步数和下一步计数位置了
            if (i == countIndex) {
                // 更新 countIndex 为下一步能够到达的最远位置
                countIndex = maxIndex;
                // 增加跳跃次数
                result++;
            }
        }
        // 最后一次跳跃
        result++;
        return result;
    }
}
