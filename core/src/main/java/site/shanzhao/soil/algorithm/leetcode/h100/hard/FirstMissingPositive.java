package site.shanzhao.soil.algorithm.leetcode.h100.hard;

/**
 * <a href="https://leetcode.cn/problems/first-missing-positive/?envType=study-plan-v2&envId=top-100-liked">
 *     缺失的第一个正数</a><><br/>
 * <><br/>
 * <a href="https://leetcode.cn/problems/first-missing-positive/solutions/7703/tong-pai-xu-python-dai-ma-by-liweiwei1419/?envType=study-plan-v2&envId=top-100-liked">
 *     题解链接</a>
 *
 * 解题思路：
 * 1. **使用原地哈希（索引归位法）**
 *    - 将 1 ~ N 范围内的正整数 `x` 交换到 `nums[x]` 的正确位置（即 `nums[x] = x`）。
 *    - 这样，数组会变得部分有序，即如果某个索引 `i` 位置上不是 `i`，那么 `i` 就是缺失的最小正整数。
 *
 * 2. **遍历 nums，进行索引归位**
 *    - 使用 `while` 交换，让 `nums[i]` 放到 `nums[i]` 该在的位置上。
 *    - 交换条件：
 *      - `nums[i]` 在 **合法范围内（0 ≤ nums[i] < nums.length）**。
 *      - `nums[i]` 还未处于正确位置（即 `nums[i] != nums[nums[i]]`）。
 *    - 这样，每个数最多被交换一次，保证 **O(N) 复杂度**。
 *
 * 3. **遍历数组，找到第一个缺失的正整数**
 *    - 遍历 `nums[i]`，如果 `nums[i] != i`，则 `i` 就是最小缺失的正整数。
 *    - 如果 `nums` 完全匹配 `1 ~ N`，则返回 `N + 1`（数组长度外的最小正整数）。
 *
 * **时间复杂度：O(N)**（每个数最多交换一次）
 * **空间复杂度：O(1)**（原地修改，不使用额外空间）
 *
 */
public class FirstMissingPositive {

    public static void main(String[] args) {
        int[] nums = new int[]{1,2,0};
        firstMissingPositive(nums);
    }
    public static int firstMissingPositive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 1; // 空数组时，最小缺失正数必然是 1
        }

        // **第一步：将 1 ~ N 范围内的正整数归位**
        for (int i = 0; i < nums.length; i++) {
            // 交换的条件：
            // 1. nums[i] 在合法范围内（0 ≤ nums[i] < nums.length）
            // 2. 重点条件：当前位置和目标位置里的value不能相等，即 nums[i] != nums[nums[i]]，避免无限循环
            // 3. nums[i] 还未在正确位置上
            while (nums[i] >= 0 && nums[i] < nums.length && nums[i] != nums[nums[i]]) {
                swap(nums, i, nums[i]);
            }
        }

        // **第二步：找出第一个不匹配的位置**
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != i) {
                return i; // 返回第一个未匹配的位置，即最小缺失的正整数
            }
        }

        if (nums[0] == nums.length) { // 特殊情况：如果 nums 最终变为 [4,1,2,3]，nums.length = 4，需要返回 5。
            return nums.length + 1;
        }

        return nums.length; // 返回数组长度，即最小缺失的正整数
    }

    /**
     * 交换数组中的两个元素
     */
    private static void swap(int[] nums, int i, int j) {
        int tmp = nums[j];
        nums[j] = nums[i];
        nums[i] = tmp;
    }

}
