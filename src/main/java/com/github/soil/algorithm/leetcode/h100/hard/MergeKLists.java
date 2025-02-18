package com.github.soil.algorithm.leetcode.h100.hard;

import com.github.soil.algorithm.leetcode.editor.cn.ListNode;

/**
 * <a href="https://leetcode.cn/problems/merge-k-sorted-lists/?envType=study-plan-v2&envId=top-100-liked">
 *     合并K个升序链表</a>
 *
 * 思路：
 *  - 采用分治法（Divide and Conquer），每次两两合并链表，类似归并排序的合并过程。
 *  - 每轮合并后，链表数量减少一半，直到只剩下一个链表，即最终的合并结果。
 *
 * 时间复杂度：
 *  - 每次合并的复杂度是 O(N)，其中 N 是所有链表节点的总数。
 *  - 共有 O(log K) 轮合并（每轮减少一半链表数）。
 *  - 总体时间复杂度：O(N log K)。
 *
 * 空间复杂度：
 *  - 由于是原地合并，没有额外的数据结构，空间复杂度为 O(1)（递归方法则会有 O(log K) 的递归栈空间）。
 */
public class MergeKLists {

    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        // lists需要合并的链表长度
        int needMergeLength = lists.length;
        // 采用分治法，每轮合并后，链表数量减少一半
        while (needMergeLength > 1) {
            int middle = (needMergeLength + 1) / 2;  // 计算下一轮的有效链表数量
            for (int i = 0; i < needMergeLength / 2; i++) {
                ListNode l1 = lists[i];
                ListNode l2 = lists[middle + i];  // 获取对应的另一半链表
                lists[i] = mergeTwoLists(l1, l2);  // 合并两个链表，并存回原数组
            }
            needMergeLength = middle;  // 更新需要合并的链表长度数量
        }
        return lists[0];  // 最终结果存储在 lists[0] 中
    }

    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null){
            return l2;
        }
        if (l2 == null){
            return l1;
        }
        ListNode dummy = new ListNode(1);
        ListNode pre = dummy;
        while (l1 != null && l2 != null){
            if (l1.val <= l2.val){
                ListNode next = l1.next;
                l1.next = null;
                pre.next = l1;
                pre = pre.next;
                l1 = next;
            }else {
                ListNode next = l2.next;
                l2.next = null;
                pre.next = l2;
                pre = pre.next;
                l2 = next;
            }
        }
        pre.next = l1 == null ? l2 : l1;
        return dummy.next;
    }
}
