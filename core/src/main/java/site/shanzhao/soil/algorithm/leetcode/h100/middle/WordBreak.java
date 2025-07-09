package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://leetcode.cn/problems/word-break/?envType=study-plan-v2&envId=top-100-liked">
 *     139. 单词拆分</a>
 *
 * 思路：
 * 1. 使用动态规划解决问题，定义状态 dp[i] 表示字符串 s 的前 i 个字符（s[0..i-1]）是否可以被字典中的单词拼接出来。
 * 2. 状态转移方程：
 *    - dp[i] = true，如果存在 j < i，使得 dp[j] = true 且 s[j..i-1] 在字典中。
 *    - 解释：
 *      - 对于每个位置 i，遍历所有可能的分割点 j（j < i）。
 *      - 如果 s[0..j-1] 可以被拼接出来（dp[j] = true），且 s[j..i-1] 在字典中，则 dp[i] = true。
 * 3. 初始化：
 *    - dp[0] = true，表示空字符串可以被拼接出来。
 * 4. 使用一个哈希集合 wordSet 存储字典中的单词，方便快速查找。
 * 5. 使用 maxLength 记录字典中最长单词的长度，优化内层循环的范围。
 *
 * 时间复杂度：O(n * m)，其中 n 是字符串 s 的长度，m 是字典中最长单词的长度。
 *    - 外层循环遍历字符串 s 的每个位置，时间复杂度为 O(n)。
 *    - 内层循环遍历所有可能的分割点 j，时间复杂度为 O(m)。
 *
 * 空间复杂度：O(n + k)，其中 n 是字符串 s 的长度，k 是字典中单词的总长度。
 *    - dp 数组需要 O(n) 的空间。
 *    - wordSet 需要 O(k) 的空间存储字典中的单词。
 */
public class WordBreak {

    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>();
        // 记录字典中最长单词的长度，用于优化内层循环的范围
        int maxLength = -1;
        for (String word : wordDict) {
            maxLength = Math.max(word.length(), maxLength);
            wordSet.add(word);
        }
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1;i <= s.length();i++){
            // 被拆分出来的右边字符串至少要小于等于 wordDict中最长的字符串，以此来跳过肯定为false的解
            for (int j = Math.max(0, i - maxLength); j < i; j++){
                if (dp[j] && wordSet.contains(s.substring(j,i))){
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[s.length()];
    }
}
