package site.shanzhao.soil.algorithm.leetcode;

import java.util.*;

/**
 * 滑动窗口解法
 * @author tanruidong
 * @date 2022/04/18 22:35
 */
public class LongestSubstringWithoutRepeatingCharacters {

    public static void main(String[] args) {
        lengthOfLongestSubstring("dvdf");
        lengthOfLongestSubstring("abcabcbb");
        lengthOfLongestSubstring("abba");
        lengthOfLongestSubstring("");
    }
    public static int lengthOfLongestSubstring(String s) {
        if (s.length() == 0){
            return 0;
        }
        Map<Character, Integer> map = new HashMap<>();
        int maxLength = 0;
        int leftIndex = 0;
        int leftIndexStr = 0;
        int rightIndexStr = 0;
        String maxStr = null;
        for (int i = 0; i < s.length(); i++){
            Character c = s.charAt(i);
            if (map.containsKey(c)) { // 一旦出现重复字符，该字符的前面字串都不需要了
                leftIndex = Math.max(leftIndex, map.get(c) + 1);
            }
            map.put(c, i);
            if (i + 1 - leftIndex > maxLength){
                maxLength = i + 1 -leftIndex;
                leftIndexStr = leftIndex;
                rightIndexStr = i;
            }
        }
        maxStr = s.substring(leftIndexStr, rightIndexStr + 1);
        System.out.printf("当前字符串[%s]不重复最长字串长度为[%s]，子串为[%s]%n", s, maxLength, maxStr);
        return maxLength;
    }

}
