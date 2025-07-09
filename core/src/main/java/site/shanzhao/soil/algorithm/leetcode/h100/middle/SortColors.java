package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/sort-colors/?envType=study-plan-v2&envId=top-100-liked">
 *     75.颜色分类</a>
 *     轴值为1的三向快排
 */
public class SortColors {
    public void sortColors(int[] nums) {
        int pivotStart = 0;
        int pivotEnd = nums.length - 1;
        int pivot = 1;
        int i = 0;
        while (i <= pivotEnd) {
            if (nums[i] == pivot) {
                i++;
            }else if (nums[i] < pivot){
                swap(nums, pivotStart, i);
                pivotStart++;
                i++;
            }else{
                swap(nums, pivotEnd, i);
                pivotEnd--;
            }
        }
    }

    private void swap(int[] nums, int i, int j){
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
