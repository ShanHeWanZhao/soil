package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/minimum-path-sum/?envType=study-plan-v2&envId=top-100-liked">
 *     64. 最小路径和</a>
 */
public class MinPathSum {
    public int minPathSum(int[][] grid) {
        /*
         *
         dp[i][j] = Min(dp[i-1][j], dp[i][j-1]) + grid[i][j]
         */
        int m = grid.length;
        int n = grid[0].length;
        int[] dp = new int[n];
        dp[0] = grid[0][0];
        for (int j = 1; j < n; j++) {
            dp[j] = grid[0][j] + dp[j-1];
        }
        for (int i = 1; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (j == 0){
                    dp[j] = dp[j] + grid[i][0];
                }else{
                    dp[j] = Math.min(dp[j], dp[j-1]) + grid[i][j];
                }
            }
        }
        return dp[n-1];
    }
}
