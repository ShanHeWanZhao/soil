package site.shanzhao.soil.algorithm.leetcode.h100.simple;

/**
 * <a href="https://leetcode.cn/problems/search-insert-position/description/?envType=study-plan-v2&envId=top-100-liked">
 *     35. 搜索插入位置</a>
 *    标准的二分查找，重点
 *    1. 左右边界
 *    2. 什么时候跳出循环
 *    3. 当目标解不在某个区间时，舍弃这个区间，边界收缩
 */
public class SearchInsert {
    public int searchInsert(int[] nums, int target) {
        int left = 0;
        // 左右边界
        int right = nums.length - 1;
        int middle = 0;
        while(right >= left){ //
            middle =(left + right) / 2;;
            if (target == nums[middle]){
                return middle;
            }else if (target < nums[middle]){
                right = middle - 1;
            }else{
                left = middle + 1;
            }
        }
        return target > nums[middle] ? middle + 1 : middle;
    }
}
