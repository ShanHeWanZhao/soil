package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/longest-palindromic-substring/?envType=study-plan-v2&envId=top-100-liked">
 *     5.最长回文子串</a>
 */
public class LongestPalindrome {
    /**
     * 动态规划解法
     * dp[i][j] 表示从i到j索引段是否是回文
     dp[i][j] =
                 true,                                   i == j
                 s[i] == s[j],                           i == j - 1
                 s[i] == s[j] && dp[i+1] == dp[j-1],     i < j - 1
     */
    public String longestPalindromeByDp(String s) {
        boolean[][] dp = new boolean[s.length()][s.length()];
        int maxLength = 1;
        int startIndex = 0;
        for (int i = s.length() - 1; i >= 0; i--){
            for (int j = i; j < s.length(); j++){
                if (i == j){
                    dp[i][j] = true;
                }else if (i == j - 1){
                    dp[i][j] = s.charAt(i) == s.charAt(j);
                }else{
                    dp[i][j] = (s.charAt(i) == s.charAt(j)) && dp[i + 1][j - 1];
                }
                if (dp[i][j] && j - i + 1 > maxLength){
                    maxLength = j - i + 1;
                    startIndex = i;
                }
            }
        }
        return s.substring(startIndex, startIndex + maxLength);
    }

    /**
     * 中心扩散法
     */
    public String longestPalindrome(String s) {
        int maxLength = 1;
        int startIndex = 0;
        for (int i = 0; i < s.length(); i++){
            int currentLenght = Math.max(centerExpand(s, i, i), centerExpand(s, i, i + 1));
            if (currentLenght > maxLength){
                maxLength = currentLenght;
                startIndex = i - (currentLenght - 1 ) / 2;
            }
        }
        return s.substring(startIndex, startIndex + maxLength);
    }

    private int centerExpand(String s, int left, int right){
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

}
