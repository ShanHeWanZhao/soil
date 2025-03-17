package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/find-first-and-last-position-of-element-in-sorted-array/?envType=study-plan-v2&envId=top-100-liked">
 *     34.在排序数组中查找元素的第一个和最后一个位置</a>
 *
 * 算法思路：
 * 1. 使用二分查找找到目标值的第一个位置（左边界）。
 * 2. 使用二分查找找到目标值 + 1 的第一个位置，然后减 1 得到目标值的最后一个位置（右边界）。
 * 3. 如果目标值不存在于数组中，则返回 [-1, -1]。
 *
 * 时间复杂度：O(log N)，其中 N 是数组的长度。
 *   - 二分查找的时间复杂度为 O(log N)，算法中进行了两次二分查找。
 *
 * 空间复杂度：O(1)，只使用了常数级别的额外空间。
 */
public class SearchRange {
    public int[] searchRange(int[] nums, int target) {
        int start = leftIndex(nums, target);
        // start == nums.length表示target大于数组最大值
        if (start == nums.length || nums[start] != target) {
            return new int[] { -1, -1 };
        }
        // 存在start，必定存在end
        // 查找刚好大于目标值的待插入位置，那么它前一个位置一定是target的最右索引值
        int end = leftIndex(nums, target + 1) - 1;
        return new int[]{start, end};
    }

    /**
     * 返回值可以理解为将target插入到nums中的最小索引值，始终 >=0
     */
    private int leftIndex(int[] nums, int target){
        int left = 0;
        int right = nums.length - 1;
        while (right >= left) {
            int middle = left + (right - left) / 2;
            if (nums[middle] >= target){
                right = middle - 1; // 范围缩小到 [left, mid - 1]
            }else{
                left = middle + 1; // 范围缩小到 [mid + 1, right]
            }
        }
        // 跳出循环是，left一定等于right + 1
        return left;
    }
}
