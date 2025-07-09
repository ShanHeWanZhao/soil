package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.*;

/**
 * <a href="https://leetcode.cn/problems/top-k-frequent-elements/description/?envType=study-plan-v2&envId=top-100-liked">
 *     347. 前k个高频元素 </a>
 *
 * 题目描述：给定一个非空的整数数组，返回其中出现频率前 k 高的元素。
 *
 * 解题思路：
 * 1. **统计频率**：使用哈希表统计每个元素的出现频率。
 * 2. **最小堆**：维护一个大小为 k 的最小堆，遍历哈希表时将元素插入堆中，如果堆的大小超过 k，则弹出堆顶元素（频率最小的元素）。
 * 3. **结果提取**：最终堆中的元素即为频率前 k 高的元素。
 *
 * 时间复杂度：O(n logk)，其中 n 是数组的长度。
 * 空间复杂度：O(n)，用于存储哈希表和堆。
 */
public class TopKFrequent {

    /**
     * 方法：最小堆
     *
     * 思路：
     * 1. 使用哈希表统计每个元素的频率。
     * 2. 使用最小堆（优先队列）维护频率前 k 高的元素。
     * 3. 遍历哈希表，将元素插入堆中，如果堆的大小超过 k，则弹出堆顶元素。
     * 4. 最终堆中的元素即为频率前 k 高的元素。
     */
    public int[] topKFrequent(int[] nums, int k) {
        // 1. 统计每个元素的频率
        Map<Integer, Integer> frequentMap = new HashMap<>();
        for (int num : nums) {
            frequentMap.put(num, frequentMap.getOrDefault(num, 0) + 1);
        }

        // 2. 使用最小堆维护频率前 k 高的元素
        Queue<Integer> heap = new PriorityQueue<>(Comparator.comparingInt(frequentMap::get));
        for (Integer key : frequentMap.keySet()) {
            heap.add(key); // 插入元素
            if (heap.size() > k) {
                heap.poll(); // 如果堆的大小超过 k，弹出堆顶元素（目的是维持堆内的k个元素始终是大值）
            }
        }

        // 3. 提取结果
        int[] result = new int[k];
        int index = 0;
        while (!heap.isEmpty()) {
            result[index++] = heap.poll(); // 将堆中的元素依次放入结果数组
        }

        return result;
    }
}