package com.github.soil.algorithm.leetcode.middle;

/**
 * <a href="https://leetcode.cn/problems/h-index/description/">
 *     274. H指数</a>
 *
 *   H指数简单定义：有h篇论文被引用了至少h次，且h要最大
 *
 * 解题思路：
 * 1. 使用二分查找法来确定H指数：
 *    - 搜索范围：0到论文总数（因为H指数最大不超过论文总数）
 *    - 对于每个候选的h值，检查是否有至少h篇论文的引用次数 >= h
 *    - 根据检查结果调整二分查找的左右边界
 * 时间复杂度：O(n log n)，其中n是论文数量。二分查找O(log n)次，每次检查需要O(n)时间。
 * 空间复杂度：O(1)，只使用了常数个额外空间。
 */
public class HIndex {
    public int hIndex(int[] citations) {
        int left = 0;
        int right = citations.length;
        // 闭区间写法 [left, right]
        while(left <= right){
            int middle = left + (right - left) / 2;
            if (helper(citations, middle)){ // 当middle满足时，此时middle可能还不是最大值，调整左指针，继续寻找h
                left = middle + 1;
            }else{
                right = middle - 1;
            }
        }
        // 循环终止时 right + 1 = left
        // 根据上诉循环的定义，可知left的左边都满足helper，right的右边都不满足helper
        // 所以取left左边的最大值即为最终结果，也就是right
        return right;
    }

    /**
     * 辅助方法，检查给定的h是否可能作为H指数
     */
    private boolean helper(int[] citations, int h){
        int count = 0;
        for (int citation : citations){
            if (citation >= h){
                count++;
            }
        }
        return count >= h;
    }
}
