package com.github.soil.algorithm.leetcode.h100.hard;

import com.github.soil.algorithm.leetcode.editor.cn.ListNode;

/**
 * <a href="https://leetcode.cn/problems/reverse-nodes-in-k-group/description/?envType=study-plan-v2&envId=top-100-liked">
 *     K个一组翻转链表</a>
 *
 * 题目思路：
 * 1. 分组遍历：每次找到 `k` 个节点，翻转这 `k` 个节点，再链接回原链表。
 * 2. dummy 头节点：方便处理头节点翻转问题。
 * 3. 双指针法：
 *    - `pre`：指向上一段翻转后的尾部。
 *    - `start`：指向当前段的首个节点。
 *    - `end`：用于找到 `k` 个一组的末尾节点。
 * 4. 翻转并链接：
 *    - 断开 `end.next` 以形成一个独立的子链表，翻转后再接回原链表。
 *    - `start` 经过翻转后变为新的尾部，需连接下一段链表。
 *
 * 复杂度分析：
 * - 时间复杂度 O(n)：
 *   - 需要遍历链表找到每组 `k` 个节点，时间复杂度 O(n)。
 *   - 每组 `k` 个节点翻转的复杂度是 O(k)，共 `n/k` 组，整体仍是 O(n)。
 * - 空间复杂度 O(1)：
 *   - 仅使用了若干指针变量，没有额外的数据结构，空间复杂度 O(1)。
 */
public class ReverseKGroup {
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null){
            return null;
        }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        // 指向上一组的尾部
        ListNode pre = dummy;
        // 重点：`end`是用来找到 `k` 个一组的末尾节点。这样设计后遍历k次会刚好在这一段的末尾
        ListNode end = dummy;
        // 每组的起点
        ListNode start = dummy.next;
        while(true){
            // 让 end 指针先走 k 步，找到当前段的末尾
            for (int i = 0; i < k && end != null; i++){
                end = end.next;
            }
            if (end == null){ // 当前段不足k个，直接跳出循环
                break;
            }
            // 下一段的起始节点
            ListNode next = end.next;
            end.next = null; // 断开
            pre.next = reverse(start); // 翻转并链接到pre的末尾
            // 上面翻转后start已到末尾，从新链接下一段的开始
            start.next = next;
            // 移动这三个指针，保持start始终在end和pre之后
            end = pre = start;
            start = start.next;
        }
        return dummy.next;
    }

    // 翻转链表：null(pre) 0(cur) -> 1(next) -> 2
    private ListNode reverse(ListNode head) {
        if (head == null){
            return null;
        }
        ListNode pre = null;
        ListNode cur = head;
        while (cur != null){
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }
}
