package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/permutations/description/?envType=study-plan-v2&envId=top-100-liked">
 *     46. 全排列</a>

 * 整体思路：
 * 本题要求生成给定数组的所有可能排列。采用回溯算法（Backtracking）解决：
 * 1. 回溯算法是一种通过探索所有可能的候选解来寻找问题解的算法
 * 2. 基本思路是从一个空路径开始，逐步添加元素形成排列
 * 3. 使用递归+回溯的方式，在每一步尝试将未使用的数字加入当前路径
 * 4. 重点：使用标记数组记录每个数字是否已被使用，避免重复使用
 * 5. 当路径长度等于数组长度时，找到一个完整排列，将其添加到结果集
 *
 * 核心步骤：
 * - 选择：在当前状态下，选择一个未使用的数字加入路径
 * - 探索：递归探索这个选择后的所有可能
 * - 回溯：撤销选择，恢复状态，尝试其他可能的选择
 *
 * 时间复杂度：O(n!)，其中n是数组长度，需要生成n!个排列
 * 空间复杂度：O(n)，递归调用栈的深度和used数组的大小
 */
public class Permute {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        // 标记数组，用于记录哪些元素已经被使用
        boolean[] used = new boolean[nums.length];
        backtrace(nums, used, result, path);
        return result;
    }

    private void backtrace(int[] nums, boolean[] used, List<List<Integer>> result, List<Integer> path){
        // 终止条件：如果当前路径长度等于数组长度，说明找到了一个完整排列
        if (nums.length == path.size()){
            // 将当前排列添加到结果集（需要创建新列表，因为path会被修改）
            result.add(new ArrayList<>(path));
            return;
        }

        // 遍历所有可能的选择
        for (int i = 0; i < nums.length; i++) {
            // 如果当前数字已被使用，则跳过
            if (used[i]){
                continue;
            }

            // 做选择：将当前数字添加到路径，并标记为已使用
            path.add(nums[i]);
            used[i] = true;

            // 递归探索这个选择后的所有可能
            backtrace(nums, used, result, path);

            // 回溯：撤销选择，将当前数字标记为未使用，并从路径中移除
            used[i] = false;
            path.remove(path.size() - 1);
        }
    }
}
