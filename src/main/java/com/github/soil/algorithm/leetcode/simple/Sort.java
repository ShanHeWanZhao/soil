package com.github.soil.algorithm.leetcode.simple;

import java.util.Arrays;

/**
 * 冒泡排序
 * @author tanruidong
 * @since 2024/04/27 13:39
 */
public class Sort {
    public static void main(String[] args) {

        System.out.println(Arrays.toString(bubbleAscSort(new int[]{4, 6, 2, 15, 746, 34})));
        System.out.println(Arrays.toString(bubbleAscSort(new int[]{4, 100, 2, 15, 746, 656464})));
    }
    public static int[] bubbleAscSort(int[] target){
        for (int i = 0; i < target.length; i++){
            for (int j = i + 1;j < target.length; j++){
                if (target[i] > target[j]){
                    int tmp = target[i];
                    target[i] = target[j];
                    target[j] = tmp;
                }
            }
        }
        return target;
    }


}
