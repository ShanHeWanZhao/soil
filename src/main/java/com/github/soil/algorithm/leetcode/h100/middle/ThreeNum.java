package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/3sum/description/?envType=study-plan-v2&envId=top-100-liked">
 *     三数之和
 *   </a><><p/>
 *
 *  三维转二维。我门先定好其中一个数，那另外两个数之和就位 （0 - 第一个数），这样就类似两数之和。
 *  具体解法：先进行一个合理性校验，然后排序好后再从左到右遍历数组，索引位置为定好的第一个酥，然后只需要在其之后寻找到合适的两个数，并让这三个数之和为0即可
 *  为什么只需考虑索引之后的两个数：因为遍历是从左到右的，索引之前的数都被之前的遍历给覆盖到了
 *  此解法复杂度为 O(n的平方)，数据量过大通过不了测试
 */
public class ThreeNum {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 3) {
            return result;
        }
        Arrays.sort(nums);
        if (nums[nums.length - 1] < 0) {
            return result;
        }
        // 遍历数组
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) { // 如果这个数大于0，则其之后的数肯定都大于0，和更是大于0了，直接返回
                return result;
            }
            // 定义第2，3个数
            int left = i + 1;
            int right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) { // 找到目标
                    List<Integer> innerResult = new ArrayList();
                    innerResult.add(nums[i]);
                    innerResult.add(nums[left]);
                    innerResult.add(nums[right]);
                    if (!result.contains(innerResult)){ // 去重
                        result.add(innerResult);
                    }
                    while (left < right && nums[right] == nums[right - 1]){ // 去重
                        right--;
                    }
                    while (left < right && nums[left] == nums[left + 1]){ // 去重
                        left++;
                    }
                    // 两边都收拢，可以少一次while循环判断
                    right--;
                    left++;
                }else if (sum > 0){ // 此时和大于0，需减小最大值，right向左
                    right--;
                }else{ // sum < 0，此时和小于0，需增加最小值，left向右
                    left++;
                }

            }
        }
        return result;
    }
}
