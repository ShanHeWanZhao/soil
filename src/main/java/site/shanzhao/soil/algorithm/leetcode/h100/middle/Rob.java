package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/house-robber/description/?envType=study-plan-v2&envId=top-100-liked">
 *     198. 打家劫舍</a>
 *
 * 思路：
 * 1. 使用动态规划解决问题，定义状态 dp[i] 表示偷窃到第 i 个房屋时能够获得的最高金额。
 * 2. 状态转移方程：
 *    - dp[i] = Max(dp[i - 2] + nums[i], dp[i - 1])
 *    - 解释：
 *      - 如果偷窃第 i 个房屋，则最高金额为 dp[i - 2] + nums[i]（因为不能偷窃相邻房屋）。
 *      - 如果不偷窃第 i 个房屋，则最高金额为 dp[i - 1]。
 * 3. 初始化：
 *    - dp[0] = nums[0]（只有一间房屋时，只能偷窃它）。
 *    - dp[1] = Max(nums[0], nums[1])（两间房屋时，选择金额较大的一个）。
 * 4. 使用滚动数组优化空间复杂度，只需维护两个变量 prevPrev 和 prev 分别表示 dp[i - 2] 和 dp[i - 1]。
 *
 * 时间复杂度：O(n)，其中 n 是数组 nums 的长度。只需遍历一次数组。
 * 空间复杂度：O(1)，只使用了常数级别的额外空间。
 */
public class Rob {

    // dp[i] = Max(dp[i - 2] + nums[i], dp[i-1])
    public int rob(int[] nums) {
        if (nums.length == 1 ){
            return nums[0];
        }
        if (nums.length == 2){
            return Math.max(nums[0], nums[1]);
        }
        int prevPrev = nums[0];
        int prev = Math.max(nums[0], nums[1]);
        for (int i = 2; i < nums.length; i++) {
            int tmp = prev;
            prev = Math.max(prevPrev + nums[i], prev);
            prevPrev = tmp;
        }
        return prev;
    }
}
