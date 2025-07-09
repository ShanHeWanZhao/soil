package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/partition-labels/?envType=study-plan-v2&envId=top-100-liked">
 *     763. 划分字母区间</a>
 *
 *
 * 思路：
 *  先记录每个字母的结束位置，因为字符串只会由小写字母组成，所有用一个int[26]即可记录，比map性能更好
 *
 * 2. 遍历字符串，维护当前片段的起始位置（startIndex）和结束位置（endIndex），并不断更新endIndex为当前遍历到的字母结束位置的最大值。
 * 3. 当遍历到当前片段的结束位置时，表示当前片段可以划分，记录片段长度，并更新起始位置。
 * 4. 最终返回所有片段长度的列表。
 *
 * 时间复杂度：O(n)，其中 n 是字符串 s 的长度。需要遍历字符串两次：
 *    - 第一次遍历记录每个字母的最后出现位置。
 *    - 第二次遍历划分片段。
 * 空间复杂度：O(1)，使用了一个固定大小的数组（26 个字母）来存储最后出现位置。
 */
public class PartitionLabels {

    public List<Integer> partitionLabels(String s) {
        // 记录每个字母在字符串中最后出现的位置
        int[] wordEndIndex = new int[26];
        for (int i = 0; i < s.length(); i++) {
            wordEndIndex[s.charAt(i) - 'a'] = i;
        }

        // 存储结果的片段长度列表
        List<Integer> result = new ArrayList<>();
        // 当前片段的起始位置
        int startIndex = 0;
        // 当前片段的结束位置
        int endIndex = 0;

        // 遍历字符串
        for (int i = 0; i < s.length(); i++) {
            // 更新当前片段的结束位置为当前字符的最后出现位置
            endIndex = Math.max(endIndex, wordEndIndex[s.charAt(i) - 'a']);

            // 如果当前位置等于当前片段的结束位置，表示当前片段可以划分
            if (i == endIndex) {
                // 记录当前片段的长度
                result.add(endIndex - startIndex + 1);
                // 更新起始位置为下一个片段的起始位置
                startIndex = endIndex + 1;
            }
        }

        // 返回结果
        return result;
    }
}
