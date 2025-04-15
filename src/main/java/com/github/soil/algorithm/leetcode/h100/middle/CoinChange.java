package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.Arrays;

/**
 * <a href="https://leetcode.cn/problems/coin-change/?envType=study-plan-v2&envId=top-100-liked">
 *     322. 零钱兑换</a>
 *
 * 思路：
 * 1. 使用动态规划解决问题，定义状态 dp[i] 表示凑成金额 i 所需的最少硬币个数。
 * 2. 状态转移方程：
 *    - dp[i] = Min(dp[i], dp[i - coins[j]] + 1)，其中 0 <= j < coins.length，且 i - coins[j] >= 0。
 *    - 解释：
 *      - 对于每个金额 i，遍历所有可能的硬币面额 coins[j]。
 *      - 如果使用 coins[j] 作为其中一个硬币，则问题转化为求 dp[i - coins[j]] 的最小值加 1。
 * 3. 初始化：
 *    - dp[0] = 0，因为凑成金额 0 不需要任何硬币。
 *    - 其他 dp[i] 初始化为最大值（表示尚未计算）。
 * 4. 如果 dp[amount] 仍为最大值，说明无法凑成总金额，返回 -1；否则返回 dp[amount]。
 *
 * 时间复杂度：O(n * m)，其中 n 是总金额 amount，m 是硬币种类数 coins.length。
 *    - 外层循环遍历 1 到 amount，时间复杂度为 O(n)。
 *    - 内层循环遍历所有硬币面额，时间复杂度为 O(m)。
 *
 * 空间复杂度：O(n)，需要一个长度为 amount + 1 的数组来存储 dp 值。
 */
public class CoinChange {
     // dp[i] = Min(dp[ i - coins[j] ] + 1, dp[i])，其中 0 <= j < coins.length，且 i - coins[j] >= 0
     public int coinChange(int[] coins, int amount) {
         int[] dp = new int[amount + 1];
         Arrays.fill(dp, Integer.MAX_VALUE);
         dp[0] = 0;
         for (int i = 1;i <= amount;i++){
             for (int coin : coins) {
                 if (i - coin >= 0 && dp[i - coin] != Integer.MAX_VALUE){
                     dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                 }
             }
         }
         return dp[amount] == Integer.MAX_VALUE ? -1 : dp[amount];
     }
}
