package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://leetcode.cn/problems/longest-substring-without-repeating-characters/?envType=study-plan-v2&envId=top-100-liked">
 *    无重复字符的最长子串
 *     </a> <><p/>
 *
 *     滑动窗口解法，核心：出现重复字符时，挪动左侧索引到当前字符上一个索引位置的下一个位置
 *
 */
public class LengthOfLongestSubstring {

    public static void main(String[] args) {
        lengthOfLongestSubstring("abcabcbb");
        lengthOfLongestSubstring("bbbbb");
        lengthOfLongestSubstring("pwwkew");
        lengthOfLongestSubstring("dasdarqjkfbajsdfgqwuehqbfgfgfdjafugfqefasv");
    }

    public static int lengthOfLongestSubstring(String s) {
        if (s == null || s.isEmpty()){
            return 0;
        }
        Map<Character, Integer> map = new HashMap();
        int left = 0;
        int right = 0;
        int max = 0;
        for (int end = 0,start = 0; end < s.length(); end++){
            Character c = s.charAt(end);
            if (map.containsKey(c)){ // 核心在这，如果出现重复，说明不符合条件，需要调整start的位置到上一个重复字符的下个指针位置
                // 为什么要用Math.max：确保start不会回缩
                start = Math.max(map.get(c) + 1, start);
            }
            int currentLength = end - start + 1;
            if (currentLength > max){
                max = currentLength;
                left = start;
                right = end;
            }
            map.put(c, end);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = left; i <= right; i++){
            Character c = s.charAt(i);
            sb.append(c);
        }
        System.out.printf("无重复最长子串为：%s，其长度为：%s%n", sb, max);
        return max;
    }
}
