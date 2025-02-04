package com.github.soil.algorithm.leetcode.h100.simple;

import java.util.Arrays;

/**
 *
 * <a href="https://leetcode.cn/problems/move-zeroes/description/?envType=study-plan-v2&envId=top-100-liked">
 *  移动0：移动0到数组末尾，且保持其他数字原相对顺序不变
 *  </a>
 */
public class MoveZeroes {
    // 快慢指针
    public void moveZeroes(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int fast = 0;
        int slow = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[fast] != 0){ // 筛选出不为0的元素
                int origin = nums[fast];
                // 放到慢指针所在的数组索引位置
                nums[slow] = origin;
                if (fast > slow){ // 将fast置0，避免最后数组的末尾不为0
                    nums[fast] = 0;
                }
                slow++;
            }
            fast++;
        }
        System.out.println(Arrays.toString(nums));
    }
}
