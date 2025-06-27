package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/longest-common-subsequence/description/?envType=study-plan-v2&envId=top-100-liked">
 *     1143.最长公共子序列</a>
 *
 * 解题思路：
 * 使用动态规划（DP）解决：
 * 1. 定义状态：dp[i][j] 表示 text1[0..i-1] 和 text2[0..j-1] 的最长公共子序列长度。
 * 2. 状态转移：
 *    - 若 text1[i-1] == text2[j-1]，则 dp[i][j] = dp[i-1][j-1] + 1；
 *    - 否则，dp[i][j] = max(dp[i-1][j], dp[i][j-1])。
 * 3. 初始化：dp[0][j] = 0 和 dp[i][0] = 0，表示空字符串的 LCS 长度为 0。
 *
 *       示例如下，text1 = abcde， text2=bde
 *                  ""  b   d   e
 *              ""  0   0   0   0
 *              a   0   0   0   0
 *              b   0   1   1   1
 *              c   0   1   1   1
 *              d   0   1   2   2
 *              e   0   1   2   3
 *
 * 空间优化（类似72题的优化）：
 * 由于 dp[i][j] 仅依赖于 dp[i-1][j-1]、dp[i-1][j] 和 dp[i][j-1]，可以将二维 DP 优化为一维数组：
 * - 使用 dp[j] 表示当前行的状态，preDp临时保存前一个dp的值（即 dp[i][j-1]）。
 * - 空间复杂度从 O(m*n) 降为 O(min(m, n))。
 *
 * 时间复杂度：O(m*n)，其中 m 和 n 分别是 text1 和 text2 的长度。
 * 空间复杂度：O(min(m, n))，使用一维数组进行优化。
 */
public class LongestCommonSubsequence {

    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        // 这个dp保存的是上一层的数据，即 i - 1层
        int[] dp = new int[n + 1];
        for (int i = 1;i <= m; i++){
            // 相当于 dp[i][0]
            int preDp = dp[0];
            for (int j = 1;j <= n; j++){
                int currentDp;
                if (text1.charAt(i - 1) == text2.charAt(j - 1)){
                    currentDp = dp[j-1] + 1 ;
                }else{ // 字符不匹配，取上方或左方的最大值
                    currentDp = Math.max(dp[j], preDp);
                }
                dp[j - 1] = preDp; // 更新 dp[i][j-1]， 为下一轮做准备
                preDp = currentDp; // 更新 preDp 为 dp[i][j]
            }
            // 还需要更新最后一个值
            dp[n] = preDp;
        }
        return dp[n];
    }
}
