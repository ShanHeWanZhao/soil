package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/product-of-array-except-self/?envType=study-plan-v2&envId=top-100-liked">
 *     除自身以外数组的乘积</a>
 *
 * 核心思想：
 * 1. 先计算每个数左侧所有数的乘积，存入 `result` 数组。
 *    - 例如，对于 `nums = [1,2,3,4]`，计算后的 `result = [1,1,2,6]`。
 * 2. 再计算每个数右侧所有数的乘积，并乘到 `result` 数组中。
 *    - 例如，对于 `nums = [1,2,3,4]`，最终结果变为 `[24,12,8,6]`。
 *
 * 这样就避免了直接计算除自身以外的乘积（即不使用除法），并且只用了 O(1) 额外空间（不算返回值）。
 *
 * 时间复杂度：O(N)（两次遍历）
 * 空间复杂度：O(1)（只使用了 `result` 数组，不算额外空间）
 */
public class ProductExceptSelf {

    public int[] productExceptSelf(int[] nums) {
        if (nums == null){
            return null;
        }
        int[] result = new int[nums.length];
        // 计算左侧乘积
        result[0] = 1; // 左侧第一个数没有左边的乘积，所以初始化为 1
        for (int i = 1; i < nums.length; i++) {// 遍历依次计算当前数左边的数据
            result[i] = result[i - 1] * nums[i - 1]; // 当前数的左侧乘积 = 前一个数的左侧乘积 * 前一个数的值
        }
        // 计算右侧乘积并更新结果
        int rightMultiply = 1;
        for (int i = nums.length - 2; i >= 0; i--) {
            rightMultiply *= nums[i + 1]; // 右侧乘积 = 右边的数 * 之前累积的右侧乘积
            result[i] *= rightMultiply; // 最终结果 = 左侧乘积 * 右侧乘积
        }
        return result;
    }
}
