package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.ListNode;

/**
 * <a href="https://leetcode.cn/problems/merge-two-sorted-lists/?envType=study-plan-v2&envId=top-100-liked">
 *     合并两个有序链表</a>
 *
 * 重点：为了避免首节点特殊处理，引入dummy代替首节点
 *
 * 思路：
 * 1. 使用 虚拟头节点（dummy），避免对首节点进行特殊处理。
 * 2. 设定指针 `current` 作为新链表的当前节点，每次将较小的节点连接到 `current` 后面，并移动指针。
 * 3. 遍历结束后，直接连接剩余未合并的部分（`list1` 或 `list2`）。
 *
 * 时间复杂度：O(m + n)，m 和 n 分别是 `list1` 和 `list2` 的长度。
 * 空间复杂度：O(1)，仅使用了常数级额外空间。
 */
public class MergeTwoLists {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        // 如果其中一个链表为空，直接返回另一个
        if (list1 == null){
            return list2;
        }
        if (list2 == null){
            return list1;
        }

        // 虚拟头节点，避免处理头节点的特殊情况
        ListNode dummy = new ListNode(-1);
        ListNode current = dummy;
        // 逐步合并两个链表
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) { // 选择较小值连接到新链表
                current.next = list1;
                list1 = list1.next; // 移动 list1 指针
            } else {
                current.next = list2;
                list2 = list2.next; // 移动 list2 指针
            }
            current = current.next; // current 指针前进
        }

        // 连接剩余的部分（只可能有一个非空）
        current.next = list1 != null ? list1 : list2;

        return dummy.next; // 返回合并后的链表头（跳过 dummy）
    }
}
