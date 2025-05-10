package com.github.soil.algorithm.leetcode.h100.simple;

/**
 * <a href="https://leetcode.cn/problems/single-number/description/?envType=study-plan-v2&envId=top-100-liked">
 *     136.只出现一次的数字</a>
 *     异或：将两个数按位进行异或操作，相同变为0，不同变为1
 *     这样 相同的两个数异或后为0，0和任意数进行异或都为那个数本身。且满足交换率和结合率
 */
public class SingleNumber {
    public int singleNumber(int[] nums) {
        int target = nums[0];
        for (int i = 1;i < nums.length; i++){
            target = target ^ nums[i];
        }
        return target;
    }
}
