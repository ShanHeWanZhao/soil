package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.Arrays;

/**
 * <a href="https://leetcode.cn/problems/perfect-squares/?envType=study-plan-v2&envId=top-100-liked">
 *     279. 完全平方数</a>
 *
 * 思路：
 * 1. 使用动态规划解决问题，定义状态 dp[i] 表示组成整数 i 所需的最少完全平方数的个数。
 * 2. 状态转移方程：
 *    - dp[i] = Min(dp[i], dp[i - j * j] + 1)，其中 0 < j * j <= i。
 *    - 解释：
 *      - 对于每个整数 i，遍历所有可能的完全平方数 j * j（j * j <= i）。
 *      - 如果使用 j * j 作为其中一个完全平方数，则问题转化为求 dp[i - j * j] 的最小值加 1。
 * 3. 初始化：
 *    - dp[0] = 0，因为组成 0 不需要任何完全平方数。
 *    - 其他 dp[i] 初始化为最大值（表示尚未计算）。
 *
 * 时间复杂度：O(n * sqrt(n))，其中 n 是给定的正整数。
 *    - 外层循环遍历 1 到 n，时间复杂度为 O(n)。
 *    - 内层循环遍历所有可能的 j（j * j <= i），时间复杂度为 O(sqrt(n))。
 *
 * 空间复杂度：O(n)，需要一个长度为 n + 1 的数组来存储 dp 值。
 */
public class NumSquares {
    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= n;i++){
            // 遍历所有可能的完全平方数 j * j
            for (int j = 1;j * j <= i;j++){
                // 取最小的那个值 + 1
                dp[i] = Math.min(dp[i - j * j] + 1, dp[i]);
            }
        }
        return dp[n];
    }
}
