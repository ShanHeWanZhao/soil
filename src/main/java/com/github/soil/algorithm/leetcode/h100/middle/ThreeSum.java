package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/3sum/description/?envType=study-plan-v2&envId=top-100-liked">
 *     15.三数之和</a>

 * 解题思路：
 * 1. 排序数组后使用双指针法，将三数之和问题转化为两数之和问题。
 * 2. 固定一个数nums[i]，然后在剩余数组中使用双指针寻找两数之和等于-nums[i]的组合。
 * 3. 通过跳过重复值避免重复解，利用排序性质提前终止无效搜索。
 *
 * 时间复杂度：O(n²)，其中排序O(n log n)，双指针遍历O(n²)
 * 空间复杂度：O(1)（不考虑结果存储），O(n)（考虑排序的栈空间）
 */
public class ThreeSum {

    public List<List<Integer>> threeSum(int[] nums) {
        // 先排序是双指针法的前提
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++){
            if (nums[i] > 0){ // 提前终止：如果当前数>0，由于数组已排序，后续不可能有三数之和=0
                break;
            }
            if (i > 0 && nums[i] == nums[i - 1]){ // 去重：跳过与前一位置相同的数，避免重复解
                continue;
            }
            int left = i + 1;
            int right = nums.length - 1;
            int target = 0 - nums[i];
            while(right > left){
                if (nums[right] + nums[left] == target){ // 找到有效解
                    result.add(Arrays.asList(nums[i], nums[right], nums[left]));
                    // 左指针去重：跳过连续相同的值
                    while (left < right && nums[left + 1] == nums[left]) {
                        left++;
                    }
                    // 右指针去重：跳过连续相同的值
                    while (right > left && nums[right] == nums[right - 1]) {
                        right--;
                    }
                    // 当上述去重完毕后，新左右指针对应的nums数据还是原大小，还需缩小区间以继续寻找其他解
                    left++;
                    right--;
                }else if (nums[right] + nums[left] < target){ // 和太小，左指针右移
                    left++;
                }else{ // 和太大，右指针左移
                    right--;
                }
            }
        }
        return result;
    }
}
