package site.shanzhao.soil.algorithm.leetcode.h100.simple;

import site.shanzhao.soil.algorithm.leetcode.ListNode;

/**
 * <a href="https://leetcode.cn/problems/linked-list-cycle/?envType=study-plan-v2&envId=top-100-liked">
 *     环形链表</a>
 * 核心思想：
 *
 * 1. 使用快慢指针（Floyd 判圈算法）检测环：（在slow的视角里，自己原地不动，fast在向前移动一步，如果有环，那最终两个一定会相遇）
 *
 *    - `slow` 指针一次走一步，`fast` 指针一次走两步。
 *    - 如果链表中存在环，`fast` 最终会追上 `slow`，即 `fast == slow`。
 *    - 如果 `fast` 遇到了 `null`（即链表尾部），说明链表无环。
 *
 * 时间复杂度：O(N)（`fast` 需要 O(N) 的时间追上 `slow`，或者到达 `null`）
 * 空间复杂度：O(1)（只使用了两个指针变量）
 */
public class HasCycle {
    public boolean hasCycle(ListNode head) {
        if (head == null) {
            return false;
        }
        ListNode fast = head;
        ListNode slow = head;
        while(fast != null && fast.next != null){
            // 不能先判断，因为slow和fast都是从首节点出发，要避开首节点，所以先移动
            slow = slow.next; // 慢指针，每次走一步
            fast = fast.next.next; // 快指针，每次走两步
            if (fast == slow){ // 如果快慢指针相遇，则说明有环
                return true;
            }
        }
        // 说明 fast 遇到 null了，肯定无环
        return false;

    }
}
