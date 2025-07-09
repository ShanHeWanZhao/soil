package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/search-in-rotated-sorted-array/?envType=study-plan-v2&envId=top-100-liked">
 *     33. 搜索旋转排序数组</a>
 *
 *     核心：以middle为分界点（包含middle），它一定能将这个 [left,right] 区间拆分为至少一个区间是升序排列的。
 *     所以，我们只需判断target是否在这个升序区间内（即target>= 这半个区间的最左端值，并且 < 这半个区间的最右端值），然后舍弃掉对应的那一个区间
 */
public class Search {
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (right >= left) {
            int middle = left + (right - left)/2;
            if (nums[middle] == target){ // 找到直接返回
                return middle;
            }
            if (nums[left] <= nums[middle]){ // 左边升序
                if (nums[left] <= target && target < nums[middle]){ // target在做区间内，right左移
                    right = middle - 1;
                }else{ // 不在则舍弃掉这个区间，left右移
                    left = middle + 1;
                }
            }else{ // 右边升序
                if (nums[middle] < target && target <= nums[right]){
                    left = middle + 1;
                }else{
                    right = middle - 1;
                }
            }
        }
        return -1;
    }
}
