package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/subsets/?envType=study-plan-v2&envId=top-100-liked">
 *     78. 子集</a>

 * 使用回溯法生成所有可能的子集。核心原理：
 * 1. 每个元素有两种状态：选或不选
 * 2. 通过递归+回溯，系统地枚举所有可能的组合
 * 3. 与排列不同，子集问题需要考虑元素相对顺序，使用index参数避免重复选择
 * 4. 每一步都将当前路径添加到结果集，包括空集
 *
 * 关键点：
 * - 无需终止条件，每个路径都是有效子集
 * - 使用起始索引，保证只向后选择元素，避免重复
 * - 回溯：撤销选择，尝试其他可能性
 *
 * 时间复杂度：O(2^n)，每个元素有选与不选两种状态
 * 空间复杂度：O(n)，递归栈的最大深度
 */
public class Subsets {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        backtrace(nums, 0, result, path);
        return result;
    }

    private void backtrace(int[] nums, int index, List<List<Integer>> result, List<Integer> path){
        result.add(new ArrayList<>(path));
        // 从index开始向后遍历，避免重复生成子集
        for (int i = index; i < nums.length; i++) {
            // 选择当前元素
            path.add(nums[i]);

            // 递归生成包含当前元素的所有子集，注意下一层从i+1开始
            backtrace(nums, i + 1, result, path);

            // 回溯：移除当前元素，尝试其他可能性
            path.remove(path.size() - 1);
        }
    }
}
