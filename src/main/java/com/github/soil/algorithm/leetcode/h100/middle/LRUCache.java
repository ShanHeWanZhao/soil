package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://leetcode.cn/problems/lru-cache/?envType=study-plan-v2&envId=top-100-liked">
 *     LRU缓存</a>
 *
 * 实现思路：
 * 1. 使用 哈希表 + 双向链表 结构，保证 `get` 和 `put` 操作的时间复杂度为 O(1)。
 * 2. `get(key)`：
 *    - 如果 `key` 存在，将其移动到链表头部，表示最近被访问。
 *    - 如果 `key` 不存在，返回 `-1`。
 * 3. `put(key, value)`：
 *    - 如果 `key` 存在，更新值，并移动到头部。
 *    - 如果 `key` 不存在：
 *      - 若缓存满了，移除 最近最少使用（LRU） 的节点（即链表尾部）。
 *      - 然后插入新节点到链表头部。
 *
 * 核心：使用dummy节点，将链表变成环形结构，dummy.next为首节点，dummy.pre为尾节点，可以避免很多边界判空
 */
class LRUCache {
    // 双向链表节点
    private static class Node{
        int key;
        int value;
        Node pre;
        Node next;
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    private final int capacity;  // 最大缓存容量
    private final Map<Integer, Node> map;  // 存储 key -> Node 的映射
    private final Node dummy = new Node(0, 0);  // 哨兵节点，避免链表操作的边界判断

    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        // 初始化让dummy自己为环
        dummy.next = dummy;
        dummy.pre = dummy;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null){
            return -1;
        }
        removeNode(node);
        putHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node existNode = map.get(key);
        if (existNode != null){ // 存在则更新值，并将node放置到首节点
            existNode.value = value;
            removeNode(existNode);
            putHead(existNode);
            return;
        }
        if (map.size() >= capacity){ // 超过阈值，剔除尾节点
            Node last = dummy.pre;
            map.remove(last.key);
            removeNode(last);
        }
        // 新增node至首节点
        Node newNode = new Node(key, value);
        map.put(key, newNode);
        putHead(newNode);
    }

    // 将node从链表里断开。这里只需要断开node前后节点的指针，node内部本身的指针不用断
    private void removeNode(Node node){
        Node next = node.next;
        node.pre.next = next;
        next.pre = node.pre;
    }

    // 将node放置到首节点
    private void putHead(Node node){
        Node originHead = dummy.next;
        dummy.next = node;
        node.pre = dummy;
        node.next = originHead;
        originHead.pre = node;
    }
}