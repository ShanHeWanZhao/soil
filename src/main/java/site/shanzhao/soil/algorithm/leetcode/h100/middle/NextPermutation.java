package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/next-permutation/?envType=study-plan-v2&envId=top-100-liked">
 *     31.下一个排列</a>
 *
 * 核心思路：
 * 1. 从后向前查找第一个升序对(nums[i] < nums[i+1])，确定交换点i
 * 2. 在i右侧找到最小的大于nums[i]的数nums[j]进行交换
 * 3. 将i+1到末尾的序列反转为升序（得到最小下一个排列）
 *
 * 关键点：
 * - 降序序列表示当前是最大排列，下一个是最小排列（整体反转）
 * - 交换后右侧保持降序，反转后变为最小升序
 *
 * 时间复杂度：O(n) 最多两次遍历数组
 * 空间复杂度：O(1) 原地修改，只使用常数空间
 */
public class NextPermutation {
    public void nextPermutation(int[] nums) {
        // 步骤1：从后向前找第一个升序位置
        int first = nums.length - 2;
        while (first >= 0 && nums[first] >= nums[first + 1]) {
            first--;
        }

        // 如果不是完全降序（存在下一个排列）
        if (first >= 0) {
            // 步骤2：在first右侧找最小的大于nums[first]的数
            int second = first + 1;
            while (second < nums.length && nums[first] < nums[second]) {
                second++;
            }
            swap(nums, first, second - 1); // 交换这两个数
        }

        // 步骤3：反转first之后的序列（变为升序）
        reverse(nums, first + 1, nums.length - 1);
    }

    private void reverse(int[] nums, int left, int right){
        while (left < right) {
            swap(nums, left++, right--);
        }
    }

    private void swap(int[] nums, int i, int j){
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
