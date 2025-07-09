package site.shanzhao.soil.algorithm.leetcode.editor.cn;

import java.util.ArrayList;
import java.util.List;

public class ListNode {
    public int val;
    public ListNode next;

    public ListNode() {
    }

    public ListNode(int val) {
        this.val = val;
    }

    public ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    @Override
    public String toString() {
        List<Integer> result = new ArrayList<>();
        result.add(val);
        ListNode nextNode = next;
        while (nextNode != null){
            result.add(nextNode.val);
            nextNode = nextNode.next;
        }
        return result.toString();
    }
}
