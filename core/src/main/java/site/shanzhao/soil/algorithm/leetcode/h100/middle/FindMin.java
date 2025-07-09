package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array/description/?envType=study-plan-v2&envId=top-100-liked">
 *     153. 寻找旋转排序数组中的最小值</a>
 *
 * 该算法用于在旋转排序数组中寻找最小值。旋转排序数组是指一个有序数组在某一点进行了旋转，
 * 例如 [0,1,2,4,5,6,7] 旋转后可能变为 [4,5,6,7,0,1,2]。
 *
 * 算法思路：
 * 1. 使用二分查找的思想，通过比较中间值与数组最后一个元素的大小，确定最小值位于哪一部分。
 * 2. 如果中间值大于数组最后一个元素，则最小值位于右半部分。
 * 3. 如果中间值小于或等于数组最后一个元素，则最小值位于左半部分或当前中间值就是最小值。
 * 4. 最终返回左指针指向的元素，即为最小值。
 *
 * 时间复杂度：O(log N)，其中 N 是数组的长度。
 *   - 每次二分查找将搜索范围缩小一半，因此时间复杂度为 O(log N)。
 *
 * 空间复杂度：O(1)，只使用了常数级别的额外空间。
 */
public class FindMin {

    /**
     * 主方法，用于在旋转排序数组中寻找最小值。
     *
     * @param nums 旋转排序数组
     * @return 数组中的最小值
     */
    public int findMin(int[] nums) {
        int left = 0;
        int right = nums.length - 1;
        while (right >= left) {
            int middle = left + (right - left) / 2; // 计算中间位置
            if (nums[middle] > nums[nums.length - 1]) {
                // 如果中间值大于数组最后一个元素，则最小值位于右半部分
                left = middle + 1;
            } else {
                // 如果中间值小于或等于数组最后一个元素，则最小值位于左半部分或当前中间值就是最小值
                right = middle - 1;
            }
        }
        return nums[left]; // 返回左指针指向的元素，即为最小值
    }
}