package com.github.soil.algorithm.leetcode.h100.middle;

import com.github.soil.algorithm.leetcode.editor.cn.ListNode;

/**
 * <a href="https://leetcode.cn/problems/add-two-numbers/?envType=study-plan-v2&envId=top-100-liked">
 *     两数相加</a>
 *     重点：使用dummy避免首节点复杂的判断逻辑
 *
 * 解决方案：
 * 1. 用一个虚拟头节点 `dummy` 来简化操作，最终返回 `dummy.next`。
 * 2. 维护一个 `carry` 变量来存储进位信息。
 * 3. 遍历 `l1` 和 `l2`，相加对应的节点值，并处理进位：
 *    - `val = l1.val + l2.val + carry`
 *    - 若 `val >= 10`，说明需要进位，`carry = 1`，当前节点值设为 `val - 10`。
 *    - 否则，不需要进位，`carry = 0`。
 * 4. 当 `l1` 和 `l2` 遍历完后，若 `carry` 仍为 1，则需要在链表尾部补 `1`。
 */
public class AddTwoNumbers {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // 创建一个虚拟头节点，简化操作
        ListNode dummy = new ListNode(-1);
        ListNode current = dummy; // 指向当前计算的节点
        boolean carry = false; // 记录是否有进位

        // 遍历两个链表，直到都为空
        while (l1 != null || l2 != null) {
            // 取当前节点值，若链表已结束则视为 0
            int v1 = (l1 == null) ? 0 : l1.val;
            int v2 = (l2 == null) ? 0 : l2.val;

            // 计算当前位的和，并处理进位
            int sum = v1 + v2 + (carry ? 1 : 0);
            carry = sum >= 10; // 判断是否需要进位

            // 创建当前节点，并连接到结果链表
            current.next = new ListNode(sum % 10);
            current = current.next;

            // 移动指针到下一位
            if (l1 != null) l1 = l1.next;
            if (l2 != null) l2 = l2.next;
        }

        // 如果最后还有进位，需要补一个额外节点
        if (carry) {
            current.next = new ListNode(1);
        }

        return dummy.next;
    }
}
