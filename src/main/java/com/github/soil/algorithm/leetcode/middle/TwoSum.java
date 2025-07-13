package com.github.soil.algorithm.leetcode.middle;

import java.util.Arrays;
import java.util.HashMap;

/**
 * 两数之和
 * https://leetcode.cn/problems/two-sum/
 * @author tanruidong
 * @since 2024/04/26 21:25
 */
public class TwoSum {

    public static void main(String[] args) {
        int[] result = new int[]{3,2,4,7,9,5};
        System.out.println(Arrays.toString(violentTwoSum(result, 16)));
        System.out.println(Arrays.toString(twoSum(result, 16)));
    }
    public static int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i= 0;i < nums.length; i++){
            int num = nums[i];
            int needSearch = target - num;
            if (map.containsKey(needSearch)){
                result[0] = i;
                result[1] = map.get(needSearch);
            }else {
                map.put(num, i);
            }
        }
        return result;
    }


    // 不推荐，暴力解法，复杂度为 O（n的二次方）
    public static int[] violentTwoSum(int[] nums, int target) {
        int[] result = new int[2];
        for (int i= 0;i < nums.length; i++){
            for (int j= i+1;j < nums.length; j++){
                if(nums[i] + nums[j] == target){
                    result[0] = i;
                    result[1] = j;
                }
            }
        }
        return result;
    }
}
