package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/partition-equal-subset-sum/description/?envType=study-plan-v2&envId=top-100-liked">
 *     416.分割等和子集</a>
 *
 *       题目转化一下：是否存在子集的和等于整个数组总和的一半（sum/2）
 *
 *      定义dp[i][j] 中表示在 0到i的数组索引内，找到和为j的组合是否存在
 *      对dp[i][j]来说，就是在对nums[i]做是否选择的判断，
 *          不选择nums[i]时，即判断在0到i-1索引内，是否存在组合总和等于j的情况
 *          选择nums[i]时，因为每个数只能选一次，所以转化为了 判断在0到i-1索引内，是否存在组合总和等于j-nums[i]的情况
 *      即：dp[i][j] = dp[i-1][j] || dp[i-1][j-nums[i]]
 *      可以发现当前dp[i][j]只取决于上一行的数据，所以可以将二维数组优化一维，这个一维数组里保存的是上一行的结果。
 *      优化后转移方程为：dp[j] = dp[j] || dp[j-nums[i]]
 *
 *  最终关键点：
 *  - 总和必须为偶数才能分割
 *  - 使用一维DP数组优化空间复杂度
 *  - 必须从后往前遍历避免重复计算
 *
 *  时间复杂度：O(n×target)，其中n是数组长度，target是sum/2
 *  空间复杂度：O(target)
 */
public class CanPartition {
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }
        if (sum % 2 == 1){ // 如果总和为奇数，直接返回false
            return false;
        }
        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        // 先初始化 i = 0 的这一行数据
        dp[0] = true;
        if (nums[0] <= target){
            dp[nums[0]] = true;
        }

        for (int i = 1; i < nums.length; i++) {
            for (int j = target; j >= nums[i]; j--){ // 重点：从后向前遍历，避免被计算后的结果给影响到
                dp[j] = dp[j] || dp[j-nums[i]];
            }
            // 提前终止条件
            if (dp[target]) {
                return true;
            }
        }
        return dp[target];
    }
}
