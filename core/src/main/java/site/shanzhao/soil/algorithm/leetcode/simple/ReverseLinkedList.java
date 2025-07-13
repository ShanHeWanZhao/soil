package site.shanzhao.soil.algorithm.leetcode.simple;

import site.shanzhao.soil.algorithm.leetcode.ListNode;


/**
 * 反转链表
 * @author tanruidong
 * @date 2022/04/29 15:41
 */
public class ReverseLinkedList {

    public static void main(String[] args) {
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(3);
        ListNode node3 = new ListNode(5);
        ListNode node4 = new ListNode(7);
        ListNode node5 = new ListNode(9);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        System.out.println(node1);
        System.out.println(reverse(node1));
    }


    public static ListNode reverse(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        while (current != null) {
            ListNode next = current.next;
            // 先赋值current.next，此时prev,next,current引用没变
            current.next = prev;
            // 依次往前推进
            prev = current;
            current = next;
        }
        return prev;
    }


}

