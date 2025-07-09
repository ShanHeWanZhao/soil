package site.shanzhao.soil.algorithm.leetcode.h100.simple;

import site.shanzhao.soil.algorithm.leetcode.ListNode;

/**
 * <a href="https://leetcode.cn/problems/palindrome-linked-list/?envType=study-plan-v2&envId=top-100-liked">
 *     回文链表</a>
 *
 * 核心思想：
 * 1. 快慢指针找到链表的中点：
 *    - 通过 `fast` 和 `slow` 指针，`fast` 每次走两步，`slow` 每次走一步。
 *    - 当 `fast` 走到链表末尾时，`slow` 正好在链表的中间。
 *
 * 2. 反转链表的后半部分：
 *    - 从中点开始，将后半部分链表反转，使其方向变为从尾到头。
 *
 * 3. 前半部分和后半部分比较：
 *    - 依次比较头部和反转后的后半部分是否相同。
 *    - 如果所有节点值都相等，则链表是回文，否则不是。
 *
 * 时间复杂度：O(N)（找到中点 O(N) + 反转 O(N) + 比较 O(N)）
 * 空间复杂度：O(1)（只使用了若干指针变量，没有额外的存储）
 */
public class Palindrome {
    public boolean isPalindrome(ListNode head) {
        if (head == null){
            return false;
        }
        ListNode last = reverseNode(middleNode(head));
        // 对比前半部分和后半部分的值
        while(head != last && last != null){
            if (head.val != last.val){ // 如果对应节点的值不同，则不是回文
                return false;
            }
            // 准备比较下一个
            last = last.next;
            head = head.next;
        }
        return true;
    }

    /**
     * 使用快慢指针找到链表的中点。
     * 快指针 `fast` 每次走两步，慢指针 `slow` 每次走一步。
     * 当 `fast` 走到链表末尾时，`slow` 处于中间位置。
     */
    private ListNode middleNode(ListNode head){
        ListNode fast = head;
        ListNode slow = head;
        while(fast != null && fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow; // 返回中点
    }

    /**
     * 反转链表的后半部分
     * 类似于"反转链表"的标准做法，使用两个指针进行反转。
     */
    private ListNode reverseNode(ListNode middle){
        ListNode pre = null;
        ListNode cur = middle;
        while (cur != null){
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre; // 返回新链表的头节点（这也是原链表的尾节点）
    }
}
