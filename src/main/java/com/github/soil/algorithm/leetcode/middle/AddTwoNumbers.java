package com.github.soil.algorithm.leetcode.middle;


import com.github.soil.algorithm.leetcode.ListNode;

/**
 * 两数相加 https://leetcode-cn.com/problems/add-two-numbers/
 * @author tanruidong
 * @date 2022/04/18 17:20
 */
public class AddTwoNumbers {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(3);
        ListNode node3 = new ListNode(5);
        node1.next = node2;
        node2.next = node3;

        ListNode node4 = new ListNode(4);
        ListNode node5 = new ListNode(9);
        ListNode node6 = new ListNode(8);
        ListNode node7 = new ListNode(0);
        ListNode node8 = new ListNode(1);
        node4.next = node5;
        node5.next = node6;
        node6.next = node7;
        node7.next = node8;
        System.out.println(node1);
        System.out.println(node4);
        System.out.println(addTwo(node1, node4));
        System.out.println(addTwoNumbers(node1, node4));
    }

    public static ListNode addTwo(ListNode l1, ListNode l2) {
        int v1 = 0;
        int v2 = 0;
        ListNode l1Next = l1;
        ListNode l2Next = l2;
        boolean needCarry = false;
        ListNode result = null;
        ListNode now = null;
        do{
            v1 = l1Next == null ? 0 : l1Next.val;
            l1Next = l1Next == null ? null : l1Next.next;
            v2 = l2Next == null ? 0 : l2Next.val;
            l2Next = l2Next == null ? null : l2Next.next;
            int sum = v1 + v2;
            int value = needCarry ? sum + 1 : sum;
            int realNum;
            if (value >= 10){
                needCarry = true;
                realNum = value - 10;
            }else {
                needCarry = false;
                realNum = value;
            }
            ListNode node = new ListNode(realNum);
            if (result == null){ // first
                result = node;
            }else {
                now.next = node;
            }
            now = node;
        }while(l1Next != null || l2Next != null || needCarry);
        System.out.println("=======");
        return result;
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head = null, pre = null;
        ListNode l1Next = l1;
        ListNode l2Next = l2;
        boolean needCarry = false;
        do {
            int l1Val = l1Next == null ? 0 : l1Next.val;
            l1Next = l1Next == null ? null : l1Next.next;
            int l2Val = l2Next == null ? 0 : l2Next.val;
            l2Next = l2Next == null ? null : l2Next.next;
            int sumVal = l1Val + l2Val;
            int resultVal = needCarry ? 1 + sumVal : sumVal;
            if (resultVal >= 10){
                resultVal = resultVal - 10;
                needCarry = true;
            }else {
                needCarry = false;
            }

            if (head == null){
                head = new ListNode(resultVal);
                pre = head;
            }else {
                pre = pre.next = new ListNode(resultVal);
            }

        }while (l1Next != null || l2Next != null || needCarry);
        return head;
    }

    // 这种算法有缺陷，利用了数字相加，可能数组过长，就算使用long也可能会导致超出范围
//    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
//        long num = toNum(l1) + toNum(l2);
//        char[] chars = String.valueOf(num).toCharArray();
//        ListNode result = null;
//        for (char des : chars) {
//            int now = Integer.parseInt(String.valueOf(des));
//            result = result == null ? new ListNode(now) : new ListNode(now, result);
//        }
//        return result;
//    }
//
//    private static long toNum(ListNode l1){
//        long result = 0;
//        long position = 0;
//        ListNode next = l1;
//        do {
//            long pow = (long) Math.pow(10, position++);
//            long realValue = next.val * pow;
//            result += realValue;
//        } while ((next = next.next) != null);
//        return result;
//    }
}

