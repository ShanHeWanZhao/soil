package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.*;

/**
 * <a href="https://leetcode.cn/problems/group-anagrams/?envType=study-plan-v2&envId=top-100-liked">
 *     字母异位词分分组
 *   </a> <br/>
 * 字母异位词：两个字符串包含的字母完全相同，只是顺序不同
 * 核心：对每个字符串中的字符重新排列，字母异位词排列出来后字符串一定相同，再用hashmap存起来
 */
public class AnagramsGroup {

    public List<List<String>> groupAnagrams(String[] strs) {
        if(strs == null || strs.length == 0){
            return null;
        }
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] charArray = str.toCharArray();
            Arrays.sort(charArray);
            String key = String.valueOf(charArray);
            if (map.containsKey(key)){
                map.get(key).add(str);
            }else {
                List<String> result = new ArrayList<>();
                result.add(str);
                map.put(key, result);
            }
        }
        return new ArrayList<>(map.values());
    }
}
