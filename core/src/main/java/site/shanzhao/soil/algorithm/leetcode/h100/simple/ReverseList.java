package site.shanzhao.soil.algorithm.leetcode.h100.simple;

import site.shanzhao.soil.algorithm.leetcode.ListNode;

/**
 * <a href="https://leetcode.cn/problems/reverse-linked-list/description/?envType=study-plan-v2&envId=top-100-liked">
 *
 *     反转链表</a>
 * 核心思想：
 * 1. 通过 迭代 的方式，将链表的指向 逐步反转，让每个节点的 `next` 指向它的前一个节点。
 * 2. 维护两个指针：
 *    - `pre`（前一个节点，初始为 null）
 *    - `cur`（当前节点，从 head 开始）
 * 3. 逐个翻转每个节点的 `next` 指针，直到整个链表反转。
 *
 * 时间复杂度：O(N)  （遍历链表一次）
 * 空间复杂度：O(1)  （仅使用三个指针）
 */
public class ReverseList {
    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null){
            return head;
        }
        ListNode pre = null; // 记录前一个节点，初始为 null（新链表的尾部）
        ListNode cur = head; // 当前节点，从 head 开始遍历
        while (cur != null) {
            ListNode next = cur.next; // 先保存当前节点的下一个节点，防止链表断裂
            cur.next = pre; // 让当前节点的 next 指向前一个节点，完成反转
            pre = cur; // pre 前进，指向当前节点
            cur = next; // cur 前进，指向原来的下一个节点
        }

        return pre; // pre 指向新的头节点

    }
}
