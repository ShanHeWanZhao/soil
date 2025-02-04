package com.github.soil.algorithm.leetcode.h100.simple;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://leetcode.cn/problems/two-sum/description/">
 *     两数之和
 * </a>
 */
public class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        if (nums == null || nums.length < 2) {
            return null;
        }
        Map<Integer, Integer> map = new HashMap<>();
        // nums中可能有重复的数据
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) { // 先判断map中是否存在当前数的补数，有则可以直接返回
                return new int[]{i,map.get(target - nums[i])};
            }
            // 没有对返回元素的索引做限制，就可以不用判断是否重复
            map.put(nums[i], i);
        }
        return null;
    }
}
