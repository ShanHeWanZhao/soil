package com.github.soil.algorithm.leetcode.h100.middle;


import com.github.soil.algorithm.leetcode.ListNode;

/**
 * <a href="https://leetcode.cn/problems/linked-list-cycle-ii/description/?envType=study-plan-v2&envId=top-100-liked">
 *     环形链表</a> <p>
 *
 * 推导过程如下：
 * eg：4位置处位环首节点
 *  0 > 1 > 2 > 3 > 4 > 5 > 6 > 7 > 8 > 9 > 4
 * fast距离：f = 12
 * slow距离：s = 6
 * head到环首节点距离：a = 4
 * 环周长：b = 6
 * fast为slow速度的两倍
 * 可推出任意从head节点出发，走 a + xb(x为0或正数) 距离，一定停留在环首节点
 *
 * 第一次相遇时： f = 2s, f = s + nb(n为正整数)
 * 所以 f = 2s = 2nb(即f和s的路程一定是环周长的整数倍)，s=nb
 *
 * 所以只要让slow再走a长，即停留在首节点
 *
 * 首次相遇后，将fast从新放置到head，并且每次直走1步。slow和fast同时走，当下次再相遇时，一定在首节点
 * 因为：a + 2nb 等价于 a +nb 等价于 a + xb。<p>
 *
 * 总结：
 * 思路：
 * 1. 快慢指针（Floyd's Tortoise and Hare）
 * 2. fast每次走两步，slow每次走一步，如果有环，fast和slow必然相遇。
 * 3. 第一次相遇时，fast走的路程是slow的两倍，且相遇点距离环首节点的距离是环周长的整数倍。
 * 4. 相遇后，将fast指针重新指向head，fast和slow一起从当前位置向前走，每次走一步。下次相遇时，必定相遇在环首节点。
 * 时间复杂度：
 * O(n)，n为链表长度（最多是两倍的环的长度）。
 * 空间复杂度：
 * O(1)，使用了常数空间。
 */
public class DetectCycle {
    public ListNode hasCycle(ListNode head) {
        if (head == null) {
            return null;
        }
        // 快慢指针相遇检测环
        ListNode fast = head;
        ListNode slow = head;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow){ // 有环，跳出循环
                break;
            }
        }
        // 如果fast到达链表末尾，说明没有环
        if (fast == null || fast.next == null) {
            return null;
        }
        // fast指向head，slow不变，继续一步步走，当再次相遇时即为环的入口
        fast = head;
        while (fast != slow) {
            fast = fast.next;
            slow = slow.next;
        }
        return fast;
    }
}
