package com.github.soil.algorithm.leetcode.h100.hard;

/**
 * <a href="https://leetcode.cn/problems/longest-valid-parentheses/description/?envType=study-plan-v2&envId=top-100-liked">
 *     32.最长有效括号</a>
 *
 * 解题思路：
 * 使用动态规划求解，定义 dp[i] 表示以第i个字符结尾的最长有效括号子串长度
 *
 * 状态转移分析：
 * 1. 当 s[i] == '(' 时：
 *    - dp[i] = 0（因为有效括号必须以 ')' 结尾）
 *
 * 2. 当 s[i] == ')' 时：
 *    a) 如果 s[i-1] == '('：
 *       - 形成一对有效括号，dp[i] = dp[i-2] + 2
 *    b) 如果 s[i-1] == ')'：
 *       - 需要检查与当前 ')' 匹配的 '(' 位置：
 *         匹配位置 left = i - dp[i-1] - 1
 *       - 如果 s[left] == '('：
 *         dp[i] = dp[i-1] + 2 + (left > 0 ? dp[left-1] : 0)
 *         （加上前面可能存在的有效括号长度）
 *
 * 边界条件处理：
 * - 字符串长度小于2时直接返回0
 * - 初始化处理前两个字符的特殊情况
 *
 * 时间复杂度：O(n)，只需遍历字符串一次
 * 空间复杂度：O(n)，需要额外的dp数组
 *
 * 示例：
 * 输入：")()())"
 * 输出：4
 * 解释：最长有效括号子串是 "()()"
 */
public class LongestValidParentheses {
    public int longestValidParentheses(String s) {
        /**
         *
         dp[i] 表示以索引i结尾的字符的最长有效括号
         分情况讨论
         1 s[i] == '(', dp[i] = 0
         2 s[i] == ')'
             2.1 s[i-1] == '(', dp[i] = dp[i-2] + 2
             2.2 s[i-1] == ')'，则以 i-1 结尾的最长有效括号初始索引为 i  - dp[i-1]，如果 s[i - dp[i-1] - 1] == '('，则它能和s[i]组成一对有效括号
         所以此时 dp[i] = dp[i-1] + 2 + dp[i - dp[i-1] - 2]
             dp[i - dp[i-1] - 2]是在索引i - dp[i-1] - 1前个字符的最长有效括号长度，因为dp[i - dp[i-1] - 1] == 0，被它截断了
         */

        if(s.length() < 2){
            return 0;
        }
        int[]dp = new int[s.length()];
        if (s.startsWith("()")){
            dp[1] = 2;
        }
        int result = dp[1];
        for (int i = 2; i <s.length();i++){
            if (s.charAt(i) == '('){ // s[i] == '(', dp[i] = 0
                continue;
            }
            if (s.charAt(i - 1) == '('){ // 直接形成一对括号
                dp[i] = dp[i-2] + 2;
            }else{ // 此时 s[i-1] == ')'
                // left为应该与s[i]匹配的左括号索引，可能小于0
                int left = i - dp[i-1] - 1;
                if (left >= 0 && s.charAt(left) == '('){
                    dp[i] = dp[i-1] + 2 + (left > 0 ? dp[left - 1] : 0);
                }
            }
            result = Math.max(result, dp[i]);
        }
        return result;
    }
}
