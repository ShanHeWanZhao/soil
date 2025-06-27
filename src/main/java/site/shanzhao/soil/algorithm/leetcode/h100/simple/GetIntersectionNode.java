package site.shanzhao.soil.algorithm.leetcode.h100.simple;

import site.shanzhao.soil.algorithm.leetcode.ListNode;

/**
 * <a href="https://leetcode.cn/problems/intersection-of-two-linked-lists/description/?envType=study-plan-v2&envId=top-100-liked">
 *
 *     相交链表</a>
 * 核心思想：
 * 1. 由于链表 A 和链表 B 长度可能不同，所以它们的起点对齐方式不同，无法直接进行逐个比较。
 * 2. 但是，如果让两个指针 分别遍历 A + B 和 B + A（即一个先遍历 A，再遍历 B，另一个先遍历 B，再遍历 A），
 *    那么两个指针的总移动距离相同，并且它们一定会同时到达 第一个相交节点 或者 同时到达 null（无交点）。
 * 3. 因此，我们定义 两个指针 a 和 b：
 *    - a 指针从 `headA` 开始遍历，当到达链表末尾时，跳转到 `headB` 继续遍历。
 *    - b 指针从 `headB` 开始遍历，当到达链表末尾时，跳转到 `headA` 继续遍历。
 *    - 由于 A + B 和 B + A 长度一样，所以如果两者有交点，它们最终会在交点相遇；否则会同时变为 null。
 *
 * 时间复杂度：O(N + M)  （N 和 M 分别是链表 A 和 B 的长度）
 * 空间复杂度：O(1) （仅使用两个指针）
 */
public class GetIntersectionNode {

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null){
            return null;
        }
        ListNode a = headA;
        ListNode b = headB;
        while(a != b){  // 当 a 和 b 不相等时，继续遍历
            // 如果 a 走到链表 A 的末尾，则跳转到链表 B
            a = a == null ? headB : a.next;
            // 如果 b 走到链表 B 的末尾，则跳转到链表 A
            b = b == null ? headA : b.next;
        }
        // 返回相交节点（如果没有相交，最终会返回 null）
        return a;
    }
}
