package com.github.soil.algorithm.leetcode.h100.middle;

import com.github.soil.algorithm.leetcode.ListNode;

/**
 * <a href="https://leetcode.cn/problems/sort-list/description/?envType=study-plan-v2&envId=top-100-liked">
 *     排序链表</a>
 *
 * 该方法采用 非递归的归并排序 对链表进行排序。
 *
 * 核心思路：
 * 1. 计算链表长度：先遍历链表计算长度 `length`，为后续归并提供依据。
 * 2. 归并排序（自底向上）：
 *    - 设定步长 `i = 1, 2, 4, 8...`，每次将链表划分成长度 `i` 的小段，并进行两两合并。
 *    - 通过 `split()` 方法拆分链表，每次拆分成两个长度为 `i` 的部分。
 *    - 通过 `mergeNodes()` 方法合并两个有序链表，并将合并后的部分链接到结果链表中。
 * 3. 不断增大步长 `i`，直到遍历完整个链表，最终链表变为有序。
 *
 * 时间复杂度：O(n log n)，其中 `n` 为链表长度。
 * - 外层 `for` 循环执行 `log n` 次（步长 `i` 每次翻倍）。
 * - 内层 `while` 遍历 `O(n)` 次（拆分和合并操作）。
 * - 整体复杂度为 `O(n log n)`。
 *
 * 空间复杂度：O(1)
 * - 归并排序通常需要 O(n) 额外空间，但本实现通过原地排序，仅使用了几个额外指针，因此空间复杂度为 O(1)。
 */
public class SortList {
    public ListNode sortList(ListNode head) {
        if (head == null){
            return null;
        }
        // 计算链表长度
        int length = 0;
        ListNode cur = head;
        while(cur != null){
            cur = cur.next;
            length++;
        }
        // 自底向上归并排序
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        // 归并步长 i 从 1 开始，每次翻倍
        for (int i = 1; i < length;i = i * 2){
            // 重点要创建这两个指针
            ListNode pre = dummy;
            ListNode start = dummy.next;
            while(start != null){
                // 拆分链表
                ListNode secondStart = split(start, i);
                ListNode thirdStart = split(secondStart, i);
                // 合并两个有序链表并链接到前一个节点
                pre.next = mergeNodes(start, secondStart);
                // 遍历pre指针，让pre指针从新指向最后那个节点
                while (pre.next != null) {
                    pre = pre.next;
                }
                // 移动start到下一个待排序部分
                start = thirdStart;
            }
        }
        return dummy.next;
    }

    /**
     * 拆分链表，返回第二部分的起始节点
     */
    private ListNode split(ListNode head, int k){
        if (head == null){
            return null;
        }
        ListNode cur = head;
        // 走 `k-1` 步，找到拆分点
        while(--k > 0 && cur != null){
            cur = cur.next;
        }
        // 如果已到达链表末尾，则返回 null
        if (cur == null){
            return null;
        }
        // 记录拆分后的起点 然后再断开链接
        ListNode next = cur.next;
        cur.next = null;
        return next;
    }

    /**
     * 合并两个有序链表
     */
    private ListNode mergeNodes(ListNode head1, ListNode head2){
        if (head1 == null){
            return head2;
        }
        if (head2 == null){
            return head1;
        }
        ListNode dummy = new ListNode(0);
        ListNode pre = dummy;
        while(head1 != null && head2 != null){
           if (head1.val <= head2.val){
               ListNode next = head1.next;
               head1.next = null;
               pre.next = head1;
               head1 = next;
           }else {
               ListNode next = head2.next;
               head2.next = null;
               pre.next = head2;
               head2 = next;
           }
            pre = pre.next;
        }
        pre.next = head1 != null ? head1 : head2;
        return dummy.next;
    }
}
