package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/kth-largest-element-in-an-array/?envType=study-plan-v2&envId=top-100-liked">
 *     215. 数组中的第k个最大元素</a>
 *
 * 题目描述：在未排序的数组中找到第 k 个最大的元素。
 *
 * 解题思路：
 * 1. **桶排序**：通过统计每个元素的频率，利用桶排序的特性找到第 k 个最大元素。时间复杂度为 O(n)，但需要额外的空间。
 * 2. **最小堆**：维护一个大小为 k 的最小堆，遍历数组时将元素插入堆中，如果堆的大小超过 k，则弹出堆顶元素。最终堆顶元素即为第 k 个最大元素。时间复杂度为 O(n logk)。
 *
 * 优化建议：
 * - 如果 k 超过 nums.length / 2，可以转化为大顶堆，内部最多存储 nums.length - k + 2 个元素，进一步优化空间和时间复杂度。
 */
public class FindKthLargest {

    /**
     * 方法1：桶排序
     * 时间复杂度：O(n)，空间复杂度：O(m)，其中 m 是桶的数量。
     *
     * 思路：
     * 1. 使用一个桶数组 `buckets` 来统计每个元素的频率。
     * 2. 遍历数组，将每个元素的值加上 10000（避免负数）作为桶的索引，统计频率。
     * 3. 从桶数组的末尾开始遍历，累加频率直到找到第 k 个最大元素。
     */
    public int findKthLargestByBuckets(int[] nums, int k) {
        int[] buckets = new int[20001]; // 桶数组，用于统计每个元素的频率
        for (int num : nums) {
            buckets[10000 + num]++; // 将元素值加上 10000 作为桶的索引
        }
        for (int i = buckets.length - 1; i >= 0; i--) {
            k = k - buckets[i]; // 减去当前桶的频率
            if (k <= 0) {
                return i - 10000; // 返回第 k 个最大元素
            }
        }
        return -1; // 未找到
    }

    private final List<Integer> minHeap = new ArrayList<>(); // 最小堆

    /**
     * 方法2：最小堆
     * 时间复杂度：O(n logk)，空间复杂度：O(k)。
     *
     * 思路：
     * 1. 维护一个大小为 k 的最小堆。
     * 2. 遍历数组，将每个元素插入堆中。
     * 3. 如果堆的大小超过 k，则弹出堆顶元素（最小的元素）。
     * 4. 最终堆顶元素即为第 k 个最大元素。
     */
    public int findKthLargestByMinHeap(int[] nums, int k) {
        for (int num : nums) {
            push(num); // 插入元素
            if (minHeap.size() > k) {
                pop(); // 如果堆的大小超过 k，弹出堆顶元素
            }
        }
        return pop(); // 返回堆顶元素
    }

    /**
     * 弹出堆顶元素（最小元素）
     */
    private int pop() {
        int result = minHeap.get(0); // 堆顶元素
        minHeap.set(0, minHeap.get(minHeap.size() - 1)); // 将最后一个元素移到堆顶
        minHeap.remove(minHeap.size() - 1); // 移除最后一个元素
        siftDown(0); // 调整堆
        return result;
    }

    /**
     * 插入元素到堆中
     */
    private void push(int val) {
        minHeap.add(val); // 将元素添加到堆的末尾
        siftUp(minHeap.size() - 1); // 调整堆
    }

    /**
     * 下沉操作：调整堆，使其满足最小堆的性质
     */
    private void siftDown(int index) {
        while (index < minHeap.size()) {
            int left = index * 2 + 1; // 左子节点
            int right = index * 2 + 2; // 右子节点
            int smallest = index; // 当前节点
            // 找到当前节点、左子节点和右子节点中的最小值
            if (left < minHeap.size() && minHeap.get(left) < minHeap.get(smallest)) {
                smallest = left;
            }
            if (right < minHeap.size() && minHeap.get(right) < minHeap.get(smallest)) {
                smallest = right;
            }
            if (smallest == index) {
                return; // 如果当前节点已经是最小值，则无需调整
            }
            swap(index, smallest); // 交换当前节点和最小值节点
            index = smallest; // 继续调整
        }
    }

    /**
     * 上浮操作：调整堆，使其满足最小堆的性质
     */
    private void siftUp(int index) {
        while (index > 0) {
            int father = (index - 1) / 2; // 父节点
            if (minHeap.get(father) <= minHeap.get(index)) {
                return; // 如果父节点小于等于当前节点，则无需调整
            }
            swap(index, father); // 交换当前节点和父节点
            index = father; // 继续调整
        }
    }

    /**
     * 交换堆中的两个元素
     */
    private void swap(int i, int j) {
        int tmp = minHeap.get(i);
        minHeap.set(i, minHeap.get(j));
        minHeap.set(j, tmp);
    }
}