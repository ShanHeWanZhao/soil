package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.Arrays;

/**
 * <a href="https://leetcode.cn/problems/longest-increasing-subsequence/description/?envType=study-plan-v2&envId=top-100-liked">
 *     300.最长递增子序列</a>
 *     两种解法：
 *     1. 动态规划
 *     2. 贪心 + 二分查找（更优解，时间复杂度更好）
 */
public class LengthOfLIS {

    /**
     * 动态规划解法：
     * 思路：dp[i] 表示以 nums[i] 结尾的最长递增子序列的长度
     * 对于每个元素 nums[i]，我们检查之前所有元素 nums[j] (0 <= j < i)
     * 如果 nums[i] > nums[j]，则 dp[i] 可能是 dp[j] + 1
     * 我们需要在所有可能的 j 中找到最大值
     *
     * 即：dp[i] = Max(dp[j] + 1)     0<= j < i，且nums[i] >nums[j]
     *
     * 时间复杂度：O(n^2) - 双层循环
     * 空间复杂度：O(n) - 需要额外的dp数组
     */
    public int lengthOfLISByDp(int[] nums) {

        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);  // 每个元素本身就是一个长度为1的子序列
        int maxLength = 1;   // 全局最大值

        for (int i = 1; i < nums.length; i++) {
            int currentMax = 1;  // 初始长度就只包含它自身一个值
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    currentMax = Math.max(currentMax, dp[j] + 1);
                }
            }
            dp[i] = currentMax;
            maxLength = Math.max(maxLength, currentMax);
        }

        return maxLength;
    }

    /**
     * 贪心+二分查找解法：
     * 思路：维护一个数组fx，其中fx[i]表示长度为i+1的LIS的最后一个元素的最小可能值
     * 遍历数组时，对于每个元素，找到它在fx中的合适位置：
     * 1. 如果大于所有fx元素，则扩展LIS长度
     * 2. 否则替换第一个大于等于它的元素，保持fx数组的最小性质
     *
     * 时间复杂度：O(n log n) - 每个元素需要进行一次二分查找
     * 空间复杂度：O(n) - 需要额外的fx数组
     */
    public int lowerBoundByBinarySearch(int[] nums) {
        int[] minTailValues = new int[nums.length]; // 最长可能为nums.length
        int currentLength = 0;  // 初始值为0

        for (int num : nums) {
            int pos = binarySearchLowerBound(minTailValues, num, currentLength);
            minTailValues[pos] = num;
            if (pos == currentLength) {
                currentLength++;
            }
        }

        return currentLength;
    }

    /**
     * 二分查找第一个大于等于target的元素位置
     * 如果所有元素都小于target，则返回数组末尾位置
     */
    private int binarySearchLowerBound(int[] nums, int target, int size){
        int left = 0;
        int right = size - 1;
        while(left <= right){
            int middle = (left + right) / 2;
            if (nums[middle] >= target){
                right = middle - 1;
            }else{
                left = middle + 1;
            }
        }
        return left;
    }
}
