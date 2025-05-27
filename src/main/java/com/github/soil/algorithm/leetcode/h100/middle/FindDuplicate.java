package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/find-the-duplicate-number/description/?envType=study-plan-v2&envId=top-100-liked">
 *     287.寻找重复数</a>
 *
 * Floyd判圈算法（快慢指针法）实现
 *
 * 核心思路：
 * 1. 将数组视为链表，数组值作为next指针（存在重复数必然形成环）
 * 2. 第一阶段：快慢指针找到相遇点（证明有环）
 * 3. 第二阶段：重新初始化快指针，同速移动找到环入口（即重复数）
 *
 * 关键点：
 * - 利用数组值范围[1,n]的特性，不会越界
 * - 重复数即为环的入口点
 *
 * 时间复杂度：O(n) 最多两轮遍历
 * 空间复杂度：O(1) 只使用常数空间
 */
public class FindDuplicate {
    public int findDuplicate(int[] nums) {
        // 第一阶段：找到快慢指针相遇点
        int fast = 0, slow = 0;
        do {
            fast = nums[nums[fast]]; // 快指针每次走两步
            slow = nums[slow];       // 慢指针每次走一步
        } while (fast != slow);      // 相遇说明有环

        // 第二阶段：找到环入口（重复数）
        fast = 0; // 重置快指针
        while (fast != slow) {
            fast = nums[fast]; // 同速前进
            slow = nums[slow];
        }

        return fast; // 相遇点即为重复数
    }
}