package com.github.soil.algorithm.leetcode.h100.hard;

/**
 * <a href="https://leetcode.cn/problems/median-of-two-sorted-arrays/?envType=study-plan-v2&envId=top-100-liked">
 * 4. 寻找两个正序数组的中位数</a>
 *
 * 该算法用于在两个正序数组中找到中位数。中位数是指将两个数组合并后，位于中间位置的数（如果合并后的数组长度为偶数，则为中间两个数的平均值）。
 *
 * 算法思路：
 * 1. 使用二分查找的思想，将问题转化为在两个有序数组中寻找第 k 小的数。
 * 2. 通过比较两个数组中第 k/2 个元素的大小，排除不可能包含第 k 小元素的部分。
 * 3. 递归地在剩余部分中寻找第 k - k/2 小的元素。
 * 4. 如果其中一个数组已经遍历完，则直接在另一个数组中返回第 k 小的元素。
 * 5. 如果 k 等于 1，则返回两个数组当前元素的最小值。
 *
 * 时间复杂度：O(log(M + N))，其中 M 和 N 分别是两个数组的长度。
 *   - 每次递归将 k 的值减半，因此时间复杂度为 O(log(M + N))。
 *
 * 空间复杂度：O(1)，只使用了常数级别的额外空间。
 */
public class FindMedianSortedArrays {

    /**
     * 主方法，用于在两个正序数组中找到中位数。
     *
     * @param nums1 第一个正序数组
     * @param nums2 第二个正序数组
     * @return 两个数组的中位数
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int allLength = nums1.length + nums2.length; // 计算两个数组的总长度
        int left = findKth(nums1, 0, nums2, 0, (allLength + 1) / 2); // 找到第 (allLength + 1) / 2 小的数
        if (allLength % 2 == 0) {
            // 如果总长度为偶数，则中位数为第 (allLength + 1) / 2 小的数和第 (allLength + 2) / 2 小的数的平均值
            return (findKth(nums1, 0, nums2, 0, (allLength + 2) / 2) + left) / 2.0;
        } else {
            // 如果总长度为奇数，则中位数为第 (allLength + 1) / 2 小的数
            return left;
        }
    }

    /**
     * ============== 一定要注意，k不是索引，而是第k个数 =================
     * 辅助方法，用于在两个正序数组中找到第 k 小的数。
     *
     * @param nums1 第一个正序数组
     * @param nums1Left 第一个数组的起始索引
     * @param nums2 第二个正序数组
     * @param nums2Left 第二个数组的起始索引
     * @param k 要找的第 k 小的数
     * @return 第 k 小的数
     */
    private int findKth(int[] nums1, int nums1Left, int[] nums2, int nums2Left, int k) {
        // 如果第一个数组已经遍历完，则直接在第二个数组中返回第 k 小的数
        if (nums1Left >= nums1.length) {
            return nums2[nums2Left + k - 1];
        }
        // 如果第二个数组已经遍历完，则直接在第一个数组中返回第 k 小的数
        if (nums2Left >= nums2.length) {
            return nums1[nums1Left + k - 1];
        }
        // 如果 k 等于 1，则返回两个数组当前元素的最小值
        if (k == 1) {
            return Math.min(nums1[nums1Left], nums2[nums2Left]);
        }
        // 计算第一个数组中第 k/2 个元素的位置（且不能越界）
        int nums1End = (nums1Left + k / 2) > nums1.length ? nums1.length - 1 : nums1Left + k / 2 - 1;
        // 计算第二个数组中第 k/2 个元素的位置（且不能越界）
        int nums2End = (nums2Left + k / 2) > nums2.length ? nums2.length - 1 : nums2Left + k / 2 - 1;

        // 准备舍弃小的那部分数据

        if (nums1[nums1End] > nums2[nums2End]) { // 舍弃nums2部分
            return findKth(nums1, nums1Left, nums2, nums2End + 1, k - (nums2End - nums2Left + 1));
        } else { // 舍弃nums1部分
            return findKth(nums1, nums1End + 1, nums2, nums2Left, k - (nums1End - nums1Left + 1));
        }
    }
}