package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/palindrome-partitioning/?envType=study-plan-v2&envId=top-100-liked">
 *     131. 分割回文串</a>
 *
 *
 * 算法思路：
 * 1. 使用动态规划预处理字符串，生成一个二维数组 `palindrome`，其中 `palindrome[i][j]` 表示字符串从索引 `i` 到 `j` 的子串是否是回文串。
 * 2. 使用回溯法遍历字符串，尝试所有可能的分割方案。
 * 3. 在回溯过程中，如果当前子串是回文串，则将其加入当前路径，并递归处理剩余部分。
 * 4. 如果遍历到字符串末尾，则将当前路径加入结果集。
 * 5. 回溯时移除当前子串，尝试其他可能的分割方案。
 *
 * 时间复杂度：O(N * 2^N)，其中 N 是字符串的长度。
 *   - 动态规划预处理的时间复杂度为 O(N^2)。
 *   - 回溯法的时间复杂度为 O(2^N)，因为每个字符都有可能是分割点。
 *
 * 空间复杂度：O(N^2)，用于存储动态规划的二维数组 `palindrome`。
 */
public class Partition {

    /**
     * 主方法，用于返回所有可能的分割方案。
     *
     * @param s 输入的字符串
     * @return 所有可能的分割方案
     */
    public List<List<String>> partition(String s) {
        // 动态规划预处理，生成回文串判断数组
        boolean[][] palindrome = new boolean[s.length()][s.length()];
        for (int i = 0; i < s.length(); i++) {
            palindrome[i][i] = true; // 单个字符一定是回文串
        }
        for (int i = s.length() - 2; i >= 0; i--) {
            for (int j = i + 1; j < s.length(); j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    // 如果首尾字符相同，且中间部分是回文串，则当前子串是回文串
                    palindrome[i][j] = (j - i == 1) || palindrome[i + 1][j - 1];
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        backtrack(s, 0, result, new ArrayList<>(), palindrome); // 回溯法生成所有分割方案
        return result;
    }

    /**
     * 回溯方法，用于生成所有可能的分割方案。
     *
     * @param s 输入的字符串
     * @param index 当前处理的字符索引
     * @param result 结果集，存储所有分割方案
     * @param path 当前路径，存储当前的分割方案
     * @param palindrome 回文串判断数组
     */
    private void backtrack(String s, int index, List<List<String>> result, List<String> path, boolean[][] palindrome) {
        if (index == s.length()) {
            // 如果遍历到字符串末尾，则将当前路径加入结果集
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = index; i < s.length(); i++) {
            if (palindrome[index][i]) {
                // 如果当前子串是回文串，则将其加入路径
                path.add(s.substring(index, i + 1));
                // 递归处理剩余部分
                backtrack(s, i + 1, result, path, palindrome);
                // 回溯，移除当前子串，尝试其他分割方案
                path.remove(path.size() - 1);
            }
        }
    }
}
