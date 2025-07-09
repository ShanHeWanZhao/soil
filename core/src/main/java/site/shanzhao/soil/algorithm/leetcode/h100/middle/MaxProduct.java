package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/maximum-product-subarray/description/?envType=study-plan-v2&envId=top-100-liked">
 *     152.乘积最大子数组</a>
 *
 *     负负可得正，所以需要记录dp[i-1]的最大值和最小值，来计算dp[i]的值
 *     dpMax[i] = Max(dpMax[i-1] * nums[i], dpMin[i-1] * nums[i], nums[i])
 *     dpMin[i] = Min(dpMax[i-1] * nums[i], dpMin[i-1] * nums[i], nums[i])
 */
public class MaxProduct {

    public int maxProduct(int[] nums) {
        int[] dpMax = new int[nums.length];
        int[] dpMin = new int[nums.length];
        dpMax[0] = nums[0];
        dpMin[0] = nums[0];
        int result = dpMax[0];
        for (int i = 1; i < nums.length; i++) {
            dpMax[i] = Math.max(Math.max(dpMax[i-1] * nums[i], dpMin[i-1] * nums[i]), nums[i]);
            dpMin[i] = Math.min(Math.min(dpMax[i-1] * nums[i], dpMin[i-1] * nums[i]), nums[i]);
            result = Math.max(result, dpMax[i]);
        }
        return result;
    }
}
