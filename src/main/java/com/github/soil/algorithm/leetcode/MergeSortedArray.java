package com.github.soil.algorithm.leetcode;

import java.util.Arrays;

/**
 * 合并两个有序数组
 * @author tanruidong
 * @date 2022/04/25 16:47
 */
public class MergeSortedArray {

    public static void main(String[] args) {
        int[] nums1 = new int[]{0,1,1,2,4,7};
        int[] nums2 = new int[]{0,3,5,6,8,9,11,11,23};
        System.out.println(Arrays.toString(mergeByMax(nums1,nums2)));
        System.out.println(Arrays.toString(mergeByMax(nums2,nums1)));
    }

    public static int[] mergeByMax(int[] nums1, int[] nums2) {
        int i = nums1.length - 1;
        int j = nums2.length - 1;
        int m = nums1.length + nums2.length;
        int[] result = new int[m];
        while (m > 0){
            if (i >=0 && nums1[i] >= nums2[j]){
                result[--m] = nums1[i--];
            }else {
                result[--m] = nums2[j--];
            }
        }
        return result;
    }

}
