package com.github.soil.algorithm.leetcode.middle;

/**
 * <a href="https://leetcode.cn/problems/gas-station/description">
 *     134. 加油站</a>
 *
 * 思路：贪心
 * 1. 核心：如果总油量大于等于总消耗量，那么必定存在一个点，从此点出发可绕完全程
 * 2. 两个变量：
 *    - delta：从当前候选起点出发的油量剩余
 *    - remainGas：整个环形路线的总油量剩余
 * 3. 遍历每个加油站时：
 *    - 计算净油量变化（gas[i] - cost[i]）
 *    - 更新delta和remainGas
 *    - 如果delta变为负数，说明当前候选起点不可行，将下一个站点设为新候选
 * 4. 最后检查remainGas，如果非负则返回候选起点，否则返回-1
 *
 * 时间复杂度：O(n) ：一次遍历
 * 空间复杂度：O(1)
 *
 */
public class CanCompleteCircuit {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int delta = 0;
        int remainGas = 0;
        int start = 0;
        for (int i = start;i < gas.length;i++){
            int cur = gas[i] - cost[i];
            delta += cur;
            remainGas += cur;
            // 如果当前delta为负，重置起点为下一站
            if (delta < 0){
                start = i + 1;
                delta = 0;
            }
        }
        // 总油量足够则返回找到的起点，否则返回-1
        return remainGas < 0 ? -1 : start;
    }
}
