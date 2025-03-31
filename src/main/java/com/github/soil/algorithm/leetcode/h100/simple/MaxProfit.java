package com.github.soil.algorithm.leetcode.h100.simple;

/**
 * <a href="https://leetcode.cn/problems/best-time-to-buy-and-sell-stock/?envType=study-plan-v2&envId=top-100-liked">
 *     121. 买卖股票的最佳时机</a>
 *
 *  我们尝试每天都卖出，要让当天卖出的利润为最大值，则需要知道在这之前股票的最小值。
 *  所以我们记录卖出股票那天之前的最小值为 Math.min(price[j])，则当天卖出的最大利润为 price[i] - Math.min(price[j]),其中 0 <= j < i
 *  所以我们只需遍历一次，同时记录股票最小值和最大利润值，不断的计算比较就可得出最终结果
 *
 * 时间复杂度：O(n)，其中 n 是数组 prices 的长度。只需要遍历一次数组。
 * 空间复杂度：O(1)，只使用了常数级别的额外空间。
 */
public class MaxProfit {
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int result = 0;
        for (int price : prices) {
            if (price <= minPrice){
                minPrice = price;
            }else{
                result = Math.max(price - minPrice, result);
            }
        }
        return result;
    }
}
