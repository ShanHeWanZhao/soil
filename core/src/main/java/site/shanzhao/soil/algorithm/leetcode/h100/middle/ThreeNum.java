package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/3sum/description/?envType=study-plan-v2&envId=top-100-liked">
 *     三数之和
 *   </a><><p/>
 *
 *  先排序，然后固定一个数，转化为「两数之和」问题。
 *
 *  关键点：排序 + 双指针收缩 + 去重处理
 *
 *  解法思路：
 *  1. 先排序，确保有序性，便于双指针查找。
 *  2. 遍历数组，每次固定一个数，在其右侧用双指针查找满足条件的两数。
 *  3. 关键：去重处理，避免相同三元组重复：
 *     - 固定数去重：跳过相同元素
 *     - 左指针去重：跳过相同元素
 *     - 右指针去重：跳过相同元素
 *  时间复杂度 O(n²)。
 *
 *  解法不难，一定要注意去重判断，3个指针都要操作去重判断
 */
public class ThreeNum {
    public List<List<Integer>> threeSum(int[] nums) {
        if (nums == null || nums.length < 3) {
            return null;
        }
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0){
                return result;
            }
            if (i > 0 && nums[i] == nums[i - 1]){ // 跳过重复元素，避免相同三元组
                continue;
            }
            int left = i + 1;
            int right = nums.length -  1;
            while(left < right){
                if (nums[i] + nums[left] + nums[right] == 0){
                    List<Integer> ans = Arrays.asList(nums[i], nums[left], nums[right]);
                    result.add(ans);
                    left++;
                    right--;
                    while(left < right && nums[left] == nums[left - 1]){ // 跳过left端的重复元素
                        left++;
                    }
                    while(right > left && nums[right] == nums[right + 1]){// 跳过right端的重复元素
                        right--;
                    }
                }else if (nums[i] + nums[left] + nums[right] < 0){ // 和小于0，指针右移
                    left++;
                }else{// 和小于0，指针左移
                    right--;
                }
            }
        }
        return result;
    }
}
