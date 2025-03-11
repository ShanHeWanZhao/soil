package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *<a href="https://leetcode.cn/problems/combination-sum/description/?envType=study-plan-v2&envId=top-100-liked">
 *     39. 组合总和</a>
 *
 * 本题使用回溯算法找出所有和为target的组合。关键特点：
 * 1. 数组中的元素可以重复使用
 * 2. 解集不能包含重复的组合
 * 3. 通过排序+剪枝优化搜索效率
 *
 * 核心策略：
 * - 排序候选数组，便于剪枝
 * - 使用回溯法尝试所有可能的组合
 * - 通过传递当前索引i而非i+1，允许元素重复使用
 * - 当剩余目标值小于当前元素时提前剪枝
 *
 * 时间复杂度：O(n^(target/min))，其中min是数组中最小的数
 * 空间复杂度：O(target/min)，递归栈的最大深度
 */
public class CombinationSum {

    public static void main(String[] args) {
        CombinationSum combinationSum = new CombinationSum();
        combinationSum.combinationSum(new int[]{5,3,2,7}, 7);
    }
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        // 排序数组，便于剪枝
        Arrays.sort(candidates);
        backtrace(candidates, target, 0, result, path);
        System.out.println(result);
        return result;

    }

    private void backtrace(int[] candidates, int target, int index, List<List<Integer>> result, List<Integer> path) {
        // 找到一个有效组合
        if (target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        // 尝试从index开始的每个候选数
        for (int i = index; i < candidates.length; i++) {
            // 剪枝：如果当前数字已经大于剩余目标值，后面更大的数字也不需要尝试
            if (target - candidates[i] < 0){
                break;
            }

            // 选择当前数字
            path.add(candidates[i]);

            // 递归探索（注意：传递i而非i+1，允许重复使用当前元素）
            backtrace(candidates, target - candidates[i], i, result, path);

            // 回溯，撤销选择
            path.remove(path.size() - 1);
        }
    }
}
