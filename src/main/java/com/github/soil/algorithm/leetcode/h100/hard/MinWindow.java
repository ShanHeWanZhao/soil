package com.github.soil.algorithm.leetcode.h100.hard;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://leetcode.cn/problems/minimum-window-substring/description/?envType=study-plan-v2&envId=top-100-liked">
 *     76.最小覆盖子串</a>
 *
 * 滑动窗口（双指针）解法：
 * 核心优化点：通过needCount计数实现O(1)时间判断窗口是否包含全部T字符
 *
 * 关键思路分解：
 * 1. 哈希表预处理：统计T中字符出现次数（空间换时间）
 * 2. 滑动窗口扩展：右指针遍历时动态更新字符计数
 * 3. 即时判断优化：needCount==0时表示当前窗口已覆盖T
 * 4. 左边界收缩：当窗口有效时，移动左边界寻找最小窗口，
 *    通过检查map[左边界字符]>=0实现O(1)时间判断该字符是否关键字符
 *
 * 时间复杂度：O(M+N)
 *  - M为S长度（左右指针各遍历一次）
 *  - N为T长度（预处理哈希表）
 * 空间复杂度：O(C)
 *  - C为字符集大小（ASCII码最多128/256）
 */
public class MinWindow {
    public String minWindow(String s, String t) {
        // 初始化哈希表（存储T字符缺口数量）
        Map<Character, Integer> map = new HashMap<>();
        int needCount = t.length(); // 关键优化点：总缺口计数器

        // 预处理：统计T字符出现次数（正数表示需要匹配的次数）
        for (char c : t.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }

        int start = -1, minLen = Integer.MAX_VALUE;
        int left = 0; // 滑动窗口左指针

        // 滑动窗口右指针扩展
        for (int right = 0; right < s.length(); right++) {
            char rc = s.charAt(right);

            /* 核心逻辑1：右指针处理 */
            if (map.getOrDefault(rc, 0) > 0) {
                needCount--; // 只有T中字符才会减少总缺口
            }
            map.put(rc, map.getOrDefault(rc, 0) - 1); // 所有字符都记录（非T字符会变负数）

            /* 核心逻辑2：needCount==0时O(1)判断窗口有效性 */
            while (needCount == 0) {
                // 更新最小窗口
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    start = left;
                }

                /* 核心逻辑3：左边界收缩时的O(1)判断 */
                char lc = s.charAt(left);
                if (map.get(lc) >= 0) {  // ≥0表示是T中的关键字符
                    needCount++; // 移出关键字符需要增加缺口
                }
                map.put(lc, map.get(lc) + 1); // 恢复字符计数
                left++;
            }
        }

        return start == -1 ? "" : s.substring(start, start + minLen);
    }
}
