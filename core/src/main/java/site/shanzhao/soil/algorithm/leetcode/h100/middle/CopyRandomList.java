package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.Node;

/**
 * <a href="https://leetcode.cn/problems/copy-list-with-random-pointer/description/?envType=study-plan-v2&envId=top-100-liked">
 *  随机链表的复制
 * </a>
 *
 * 核心思想：
 * 1. 复制节点并插入原链表：遍历原链表，在每个节点后插入一个复制节点，使原链表变成交替的形式。
 * 2. 复制随机指针：遍历原链表，如果当前节点有 `random` 指针，则复制节点的 `random` 指针指向 `cur.random.next`。
 *      我们可以很方便的拿到原节点的复制节点，则根据前面的操作，originNode.random.next即为其复制节点里的复制random指针
 * 3. 拆分链表：将原链表和复制链表分离，恢复原链表，并返回复制链表的头节点。
 *
 * 时间复杂度：O(n)，遍历链表三次，每次的复杂度都是 O(n)。
 * 空间复杂度：O(1)，没有使用额外的数据结构，仅在链表上进行操作。
 */
public class CopyRandomList {

    public Node copyRandomList(Node head) {
        if (head == null) {
            return null;
        }
        // **第一步：复制节点，并插入到原链表中**
        // 例如：A -> B -> C 变成 A -> A' -> B -> B' -> C -> C'
        Node cur = head;
        while (cur != null) {
            Node copyNode = new Node(cur.val);
            Node next = cur.next;
            cur.next = copyNode;
            copyNode.next = next;
            cur = next;
        }
        // **第二步：复制随机指针**
        cur = head;
        while (cur != null) {
            Node random = cur.random;
            if (random != null) {
                cur.next.random = random.next; // 复制节点的 `random` 指针
            }
            cur = cur.next.next; // 跳过复制节点，移动到下一个原链表节点
        }
        // **第三步：拆分链表**
        // 例如：A -> A' -> B -> B' -> C -> C' 变成 A -> B -> C 和 A' -> B' -> C'
        Node dummy = new Node(0); // 用于保存原链表头
        Node pre = dummy;
        pre.next = head;
        Node copyDummy = new Node(0); // 用于保存复制链表头
        Node copyPre = copyDummy;
        int count = 0; // 计数器，偶数为原链表，奇数为复制链表
        while (head != null) {
            // 备份下一个节点 并断开链接
            Node next = head.next;
            head.next = null;
            if (count % 2 == 0) {  // 连接到原链表
                pre.next = head;
                pre = pre.next;
            } else {  // 连接到复制链表
                copyPre.next = head;
                copyPre = copyPre.next;
            }
            head = next;
            count++;
        }
        return copyDummy.next;
    }
}
