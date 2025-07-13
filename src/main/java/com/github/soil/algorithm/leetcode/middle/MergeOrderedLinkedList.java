package com.github.soil.algorithm.leetcode.middle;

import com.github.soil.algorithm.leetcode.ListNode;

/**
 * @author tanruidong
 * @since 2024/05/21 21:19
 */
public class MergeOrderedLinkedList {
    public static void main(String[] args) {

    }

    private int[] merge(int[]... arrs){
        int size = 0;
        for (int[] arr : arrs) {
            size = arr.length + size;
        }
        int[] result = new int[size];

        return result;
    }

}
