package com.github.soil.algorithm.leetcode.simple;

/**
 * 二分查找
 * @author tanruidong
 * @since 2024/04/26 23:51
 */
public class BinarySearch {
    public static void main(String[] args) {
//        System.out.println(ascSearch(new int[]{1,3,5,7,9}, 1));
//        System.out.println(ascSearch(new int[]{1,3,5,7,9,155,211,5345,423424}, 155));
//        System.out.println(ascSearch(new int[]{1,3,5,7,9}, 5));
//        System.out.println(ascSearch(new int[]{1,3,5,7,9}, 7));
        System.out.println("降序开始================");
        System.out.println(descSearch1(new int[]{5243,321,320,188,44}, 44));
        System.out.println(descSearch1(new int[]{9,8,7,6,5,4,3,1}, 10));
        System.out.println(descSearch1(new int[]{11,9,7,5,3}, 9));
    }

    public static int descSearch1(int[] nums, int target) {
        int index = -1;
        int leftIndex = 0;
        int rightIndex = nums.length - 1;
        int middleIndex;
        while(leftIndex <= rightIndex){
            middleIndex = (leftIndex + rightIndex) / 2;
            int middle = nums[middleIndex];
            if (target == middle){
                index = middleIndex;
                break;
            }else if (target > middle){
                rightIndex = middleIndex - 1;
            }else {
                leftIndex = middleIndex + 1;
            }
        }
        return index;
    }


    public static int descSearch(int[] nums, int target) {
        int leftIndex = 0;
        int rightIndex = nums.length;
        int middle;
        while(leftIndex <= rightIndex){
            middle = (leftIndex + rightIndex) / 2;
            if (nums[middle] == target){
                return middle;
            }else if (nums[middle] > target){
                leftIndex = middle + 1;
            }else {
                rightIndex = middle - 1;
            }
        }
        return -1;
    }


    public static int ascSearch(int[] nums, int target) {
        int leftIndex = 0;
        int rightIndex = nums.length - 1;
        int middle;
        while(leftIndex <= rightIndex){
            middle = (leftIndex + rightIndex) / 2;
            if (nums[middle] == target){
                return middle;
            }else if (nums[middle] > target){
                rightIndex = middle - 1;
            }else {
                leftIndex = middle + 1;
            }
        }
        return -1;
    }

}
