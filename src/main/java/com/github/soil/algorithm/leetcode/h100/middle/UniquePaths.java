package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.Arrays;

/**
 * <a href="https://leetcode.cn/problems/unique-paths/?envType=study-plan-v2&envId=top-100-liked">
 *     62. 不同路径</a>
 *
 *     dp[i][j] 表示到第i行j列有多撒后条不同路径
 *     dp[i][j] = dp[i-1][j] + dp[i][j-1]     表示可从左或上到达当前位置
 *     dp[i][j]其实只由左边和上边的值决定，可进行空间优化为：dp[j] = dp[j] + dp[j-1];
 */
public class UniquePaths {
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        // 测试用例中将 start=end 也记为1条路径
        Arrays.fill(dp, 1);
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] = dp[j] + dp[j-1];
            }
        }
        return dp[n - 1];
    }
}
