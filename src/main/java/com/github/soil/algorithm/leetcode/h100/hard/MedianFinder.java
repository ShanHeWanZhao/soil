package com.github.soil.algorithm.leetcode.h100.hard;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * <a href="https://leetcode.cn/problems/find-median-from-data-stream/?envType=study-plan-v2&envId=top-100-liked">
 *    295. 数据流的中位数 </a>
 *
 * 思路：
 * 1. 使用两个堆来维护数据流中的元素：
 *    - 最大堆（maxHeap）：存储较小的一半元素，堆顶是较小元素中的最大值。
 *    - 最小堆（minHeap）：存储较大的一半元素，堆顶是较大元素中的最小值。
 * 2. 保持两个堆的大小平衡：
 *    - 当元素总数为偶数时，两个堆的大小相等，中位数为两个堆顶元素的平均值。
 *    - 当元素总数为奇数时，最大堆比最小堆多一个元素，中位数为最大堆的堆顶元素。
 * 3. 添加元素时，根据当前元素的大小决定将其放入哪个堆，并保持两个堆的大小平衡。
 *
 * 时间复杂度：
 * - addNum(int num)：O(log n)，每次插入堆的时间复杂度为 O(log n)。
 * - findMedian()：O(1)，直接访问堆顶元素。
 *
 * 空间复杂度：O(n)，需要存储所有元素。
 */
public class MedianFinder {
    // 最小堆，存储较大的一半元素
    private Queue<Integer> minHeap;
    // 最大堆，存储较小的一半元素
    private Queue<Integer> maxHeap;

    public MedianFinder() {
        // 初始化最小堆，默认是小顶堆
        minHeap = new PriorityQueue<>();
        // 初始化最大堆，通过自定义比较器实现大顶堆
        maxHeap = new PriorityQueue<>((num1, num2) -> num2 - num1);
    }

    /**
     * 向数据结构中添加一个整数
     * @param num 要添加的整数
     */
    public void addNum(int num) {
        if (maxHeap.isEmpty()) { // 大顶堆为空，则整个数据结构就没有数据，则添加第一个数据
            maxHeap.add(num);
            return;
        }

        // 如果当前元素总数为偶数
        if (size() % 2 == 0) {
            // 如果最小堆的堆顶元素大于等于当前元素，说明当前元素属于较小的一半，放入最大堆
            if (minHeap.peek() >= num) {
                maxHeap.add(num);
            } else {
                // 否则，将最小堆的堆顶元素移动到最大堆，并将当前元素放入最小堆
                maxHeap.add(minHeap.poll());
                minHeap.add(num);
            }
        } else {
            // 如果当前元素总数为奇数
            // 如果最大堆的堆顶元素小于等于当前元素，说明当前元素属于较大的一半，放入最小堆
            if (maxHeap.peek() <= num) {
                minHeap.add(num);
            } else {
                // 否则，将最大堆的堆顶元素移动到最小堆，并将当前元素放入最大堆
                minHeap.add(maxHeap.poll());
                maxHeap.add(num);
            }
        }
    }

    /**
     * 返回当前数据结构中所有元素的中位数
     * @return 中位数
     */
    public double findMedian() {
        // 如果元素总数为偶数，返回两个堆顶元素的平均值
        // 如果元素总数为奇数，返回最大堆的堆顶元素
        return size() % 2 == 0 ? (minHeap.peek() + maxHeap.peek()) / 2.0 : maxHeap.peek();
    }

    /**
     * 返回当前数据结构中元素的总数
     * @return 元素总数
     */
    private int size() {
        return minHeap.size() + maxHeap.size();
    }
}