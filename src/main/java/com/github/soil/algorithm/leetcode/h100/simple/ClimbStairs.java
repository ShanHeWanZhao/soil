package com.github.soil.algorithm.leetcode.h100.simple;

/**
 * <a href="https://leetcode.cn/problems/climbing-stairs/?envType=study-plan-v2&envId=top-100-liked">
 *     70. 爬楼梯</a>
 *
 *     状态转移方程: dp[i] = dp[i - 2] + dp[i - 1]
 *     dp[i]表示爬到i层一共有多少种方法。因为要爬到i层，只能从i - 2 爬2个台阶直接到 i 或者从 i- 1爬1个台阶到达
 */
public class ClimbStairs {

    public int climbStairs(int n) {
        if (n < 3){
            return n;
        }

        int prev = 1;
        int current = 2;
        for (int i = 3; i <= n; i++){
            int tmp = current;
            current = prev + current;
            prev = tmp;

        }
        return current;
    }

}
