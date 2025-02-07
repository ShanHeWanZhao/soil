package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/maximum-subarray/description/?envType=study-plan-v2&envId=top-100-liked">
 * 最大子数组和</a> <><p/>
 * <p>
 * 解题思路：
 * 1. 动态规划思路：将大问题拆解为子问题
 * 2. 对数组中的每个位置i，计算"以位置i结尾的最大子数组和"
 * 3. 所有位置的最大子数组和中的最大值就是答案
 * <p>
 * 状态转移分析：
 * 1. 对于位置i，最大子数组和有两种可能：
 * - 只包含当前数字nums[i]
 * - 将当前数字nums[i]接在前一个位置的最大子数组后面
 * 2. 如果前一个位置的最大和为正，则可以接在后面获得更大的和
 * 3. 如果前一个位置的最大和为负，则直接使用当前数字更优
 * <p>
 * 时间复杂度：O(N)，只需遍历一次数组
 * 空间复杂度：O(1)，只使用常数额外空间
 */
public class MaxSubArray {

    public int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0){
            return 0;
        }
        int max = nums[0];
        // 前一个位置结尾的最大子数组和
        int preMaxSum = 0;
        for (int num : nums) {
            // 当前位置的最大子数组和
            int currentMaxNum;
            if (preMaxSum > 0){ // 有增益
                currentMaxNum = preMaxSum + num;
            }else { // 无增益，则丢弃前面的结果，直接使用当前数
                currentMaxNum = num;
            }
            preMaxSum = currentMaxNum;
            // 更新全局最大值
            max = Math.max(max, currentMaxNum);
        }
        return max;
    }
}
