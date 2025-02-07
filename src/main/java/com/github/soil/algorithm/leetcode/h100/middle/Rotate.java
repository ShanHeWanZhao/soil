package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/rotate-array/?envType=study-plan-v2&envId=top-100-liked">
 *     轮转数组
 *     </a>
 * <p></p>
 *     <a href="https://leetcode.cn/problems/rotate-array/solutions/2784427/tu-jie-yuan-di-zuo-fa-yi-tu-miao-dong-py-ryfv/?envType=study-plan-v2&envId=top-100-liked">
 *         解法</a>
 */
public class Rotate {
    public void rotate(int[] nums, int k) {
        if (nums == null || nums.length < 2){
            return;
        }
        int length = nums.length;
        k = k % length;
        innerRotate(nums, 0, length - 1);
        innerRotate(nums, 0, k - 1);
        innerRotate(nums, k, length - 1);
    }

    private void innerRotate(int[] nums, int start, int right){
        for (int i = start; i < right; i++, right--){
            int tmp = nums[i];
            nums[i] = nums[right];
            nums[right] = tmp;
        }
    }
}
