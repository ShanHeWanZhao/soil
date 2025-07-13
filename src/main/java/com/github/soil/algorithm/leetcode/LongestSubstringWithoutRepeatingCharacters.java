package com.github.soil.algorithm.leetcode;

import java.util.*;

/**
 * 最长不重复子串
 * @author tanruidong
 * @date 2022/04/18 22:35
 */
public class LongestSubstringWithoutRepeatingCharacters {

    public static void main(String[] args) {
        lengthOfLongestSubstring("dvdf");
        slidingWindow("dvdf");
        lengthOfLongestSubstring("abcfcgh");
        slidingWindow("abcfcgh");
        lengthOfLongestSubstring("abba");
        slidingWindow("abba");
        lengthOfLongestSubstring("");
        slidingWindow("");
    }


    /**
     * 滑动窗口解法
     * @param target
     * @return
     */
    public static int slidingWindow(String target){
        if (target == null || target.isEmpty()){
            return 0;
        }
        int leftIndex = 0;
        int leftIndexChar = 0;
        int rightIndexChar = 0;
        int maxLength = 0;
        HashMap<Character, Integer> map = new HashMap<>();
        for (int i = 0;i < target.length(); i++){
            Character c = target.charAt(i);
            if (map.containsKey(c)){ // 出现重复字符，则将leftIndex移到重复字符的下一个索引位
                leftIndex = Math.max(leftIndex, map.get(c) + 1);
            }
            map.put(c, i);
            if (i + 1 - leftIndex > maxLength){ // 每次循环都判断是否超过maxLength，以更新滑动的窗口
                rightIndexChar = i;
                leftIndexChar = leftIndex;
                maxLength = rightIndexChar - leftIndexChar + 1;
            }
        }
        System.out.printf("当前字符串[%s]不重复最长字串长度为[%s]，子串为[%s]%n", target, maxLength,  target.substring(leftIndexChar, rightIndexChar + 1));
        return maxLength;
    }


    public static int lengthOfLongestSubstring(String target) {
        if (target.isEmpty()){
            return 0;
        }
        Map<Character, Integer> map = new HashMap<>();
        int maxLength = 0;
        int leftIndex = 0;
        int leftIndexChar = 0;
        int rightIndexChar = 0;
        String maxStr = null;
        for (int i = 0; i < target.length(); i++){
            Character c = target.charAt(i);
            if (map.containsKey(c)) { // 一旦出现重复字符，该字符的前面字串都不需要了
                leftIndex = Math.max(leftIndex, map.get(c) + 1);
            }
            map.put(c, i);
            if (i + 1 - leftIndex > maxLength){
                maxLength = i + 1 -leftIndex;
                leftIndexChar = leftIndex;
                rightIndexChar = i;
            }
        }
        System.out.printf("当前字符串[%s]不重复最长字串长度为[%s]，子串为[%s]%n", target, maxLength,  target.substring(leftIndexChar, rightIndexChar + 1));
        return maxLength;
    }

}
