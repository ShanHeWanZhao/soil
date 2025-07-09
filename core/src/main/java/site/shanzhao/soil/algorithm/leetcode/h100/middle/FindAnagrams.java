package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/find-all-anagrams-in-a-string/?envType=study-plan-v2&envId=top-100-liked">
 *     找到字符串中所有字母异位词</a> <><p/>
 *    解题思路：滑动窗口 + 字符计数
 *  1. 使用大小为26的数组记录字符出现次数（因为只包含小写字母）
 *  2. 通过滑动窗口维护一个可变的字符统计子集，当子集等于目标字符统计时，找到一个异位词
 *
 *  时间复杂度：O(n)，其中n为字符串s的长度
 *  空间复杂度：O(1)，使用固定大小为26的数组
 */
public class FindAnagrams {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        // 合理校验
        if (s == null || p == null || p.length() > s.length()){
            return result;
        }
        // 初始化能表示s和p的数组，内部默认值都会为0
        int[] sCnt = new int[26];  // 记录滑动窗口中的字符计数
        int[] pCnt = new int[26];  // 记录目标字符串p的字符计数
        // 将p映射到pCnt中去（s和p都是小写字母组成的，不会超过26个）
        for (int i = 0;i < p.length(); i++){
            pCnt[p.charAt(i) - 'a'] = pCnt[p.charAt(i) - 'a'] + 1;
        }
        // 定义滑动窗口的左右指针
        for (int start = 0, end = 0; end < s.length(); end++){
            // s中当前2字符的在数组中的索引位
            int currentChar = s.charAt(end) - 'a';
            sCnt[currentChar] = sCnt[currentChar] + 1;
            // 核心，当前字母出现次数多于p中这个字母出现的次数，说明需要调整左指针向右移动，一直到这个字母的出现次数小于等于p中的这个字母出现次数
            // 最坏情况就是start调整到end位置
            // 通过如下调整，一定可以保证sCnt数组中的每个索引value都小于或等于pCnt数组对应索引位置的value（可以想象成sCnt是pCnt的子集）
            while(sCnt[currentChar] > pCnt[currentChar]){
                int startChar = s.charAt(start) - 'a';
                // start右移，也需要减少start位置出字母在sCnt的次数
                sCnt[startChar] = sCnt[startChar] - 1;
                start++;
            }
            // 当窗口的长度等于p字符串的长度，说明sCnt=pCnt（sCnt这个子集扩大了到和pCnt完全相等）
            // 因为之前的while循环保证了窗口中所有字符的数量都不超过目标字符串
            if (end - start + 1 == p.length()){
                result.add(start); // 收集结果
            }
        }
        return result;
    }
}
