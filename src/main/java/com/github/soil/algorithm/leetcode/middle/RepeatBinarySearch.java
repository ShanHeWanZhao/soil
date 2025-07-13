package com.github.soil.algorithm.leetcode.middle;

/**
 * @author tanruidong
 * @since 2024/04/26 23:51
 */
public class RepeatBinarySearch {
    public static void main(String[] args) {
        System.out.println(solution1(new int[]{1,3,5,5,5,7,9}, 5));
        System.out.println(solution2(new int[]{1,3,5,5,5,7,9}, 5));
        System.out.println(solution1(new int[]{1,3,5,7,9,13,14,15,155,155,211,5345,423424}, 155));
        System.out.println(solution2(new int[]{1,3,5,7,9,13,14,15,155,155,211,5345,423424}, 155));
        System.out.println(solution1(new int[]{7,7,9,43,56,78}, 7));
        System.out.println(solution2(new int[]{7,7,9,43,56,78}, 7));
        System.out.println(solution1(new int[]{1,3,5,7,9,9}, 9));
        System.out.println(solution2(new int[]{1,3,5,7,9,9}, 9));
    }

    public static int solution1(int[] nums, int target) {
        int leftIndex = 0;
        int rightIndex = nums.length;
        int middleIndex;
        int result = -1;
        while(leftIndex <= rightIndex){
            middleIndex = (leftIndex + rightIndex) / 2;
            if (target < nums[middleIndex]){
                rightIndex = middleIndex - 1;
            }else if (target > nums[middleIndex]){
                leftIndex = middleIndex + 1;
            }else {
                if (middleIndex == 0 || nums[middleIndex - 1] != target){
                    result = middleIndex;
                    break;
                }else {
                    rightIndex = middleIndex - 1;
                }
            }
        }
        return result;
    }
    public static int solution2(int[] nums, int target) {
        int index = binarySearch(nums, target);
        if (index == -1 || index == 0){
            return index;
        }
        while(index > 0 && nums[index - 1] == target){
            index--;
        }
        return index;
    }

    public static int binarySearch(int[] nums, int target) {
        int leftIndex = 0;
        int rightIndex = nums.length;
        int result = -1;
        int middleIndex;
        while(leftIndex < rightIndex){
            middleIndex = (leftIndex + rightIndex) / 2;
            if (nums[middleIndex] == target){
                result = middleIndex;
                break;
            }else if (nums[middleIndex] < target){
                leftIndex = middleIndex + 1;
            }else {
                rightIndex = middleIndex - 1;
            }
        }
        return result;
    }

}
