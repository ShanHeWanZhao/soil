package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/edit-distance/description/?envType=study-plan-v2&envId=top-100-liked">
 *     72.编辑距离</a>
 *
 * 解题思路：
 * 使用动态规划（DP）解决：
 * 1. 定义状态：dp[i][j] 表示 word1[0..i-1] 转换成 word2[0..j-1] 的最小操作次数。
 * 2. 状态转移：
 *    - 若 word1[i-1] == word2[j-1]，则 dp[i][j] = dp[i-1][j-1]（无需操作）；
 *    - 否则，dp[i][j] = min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]) + 1（分别对应删除i字符、在i后插入j字符、替换i为j字符 操作）。
 * 3. 初始化：
 *    - dp[0][j] = j（word1 为空，需插入 j 个字符）；
 *    - dp[i][0] = i（word2 为空，需删除 i 个字符）。
 *
 *    示例如下，word1=horse，word2=ros
 *                 ""  r   o   s
 *          ""     0   1   2   3
 *          h      1   1   2   3
 *          o      2   2   1   2
 *          r      3   2   2   2
 *          s      4   3   3   2
 *          e      5   4   4   3
 *
 * 空间优化（类似1143题的优化）：
 * 由于 dp[i][j] 仅依赖于 dp[i-1][j]、dp[i][j-1] 和 dp[i-1][j-1]，可以将二维 DP 优化为一维数组：
 * - 使用 dp[j] 表示当前行的状态，preDp临时保存前一个dp的值（即 dp[i][j-1]）。
 * - 空间复杂度从 O(m*n) 降为 O(n)。
 *
 * 时间复杂度：O(m*n)，其中 m 和 n 分别是 word1 和 word2 的长度。
 * 空间复杂度：O(n)，使用一维数组进行优化。
 */
public class MinDistance {

    public int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[] dp = new int[n + 1];
        // 初始化：word1 为空，转换为 word2[0..j-1] 需要插入 j 次
        for (int j = 1; j <= n; j++){
            dp[j] = j;
        }

        for (int i = 1;i <= m; i++){
            // 初始化 j=0 的dp值
            int preDp = dp[0] + 1;
            for (int j = 1;j <= n; j++){
                int currentDp;
                if (word1.charAt(i - 1) == word2.charAt(j - 1)){ // 字符相同，无需操作，直接继承左上角的值
                    currentDp = dp[j-1];
                }else{
                    // 字符不同，取左（插入）、上（删除）、左上（替换）的最小值 +1
                    currentDp = Math.min(Math.min(dp[j], preDp), dp[j-1]) + 1;
                }
                dp[j-1] = preDp;
                preDp = currentDp;
            }
            dp[n] = preDp;
        }
        return dp[n];

    }
}
