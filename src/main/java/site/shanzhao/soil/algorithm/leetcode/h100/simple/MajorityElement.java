package site.shanzhao.soil.algorithm.leetcode.h100.simple;

/**
 * <a href="https://leetcode.cn/problems/majority-element/?envType=study-plan-v2&envId=top-100-liked">
 *     169.多数元素</a>
 *
 * Boyer-Moore投票算法实现
 *
 * 核心思路：
 * 1. 假设第一个元素是众数，初始化计数器
 * 2. 遍历数组，相同数则计数+1，不同数则计数-1
 * 3. 当计数器归零时，重新假设当前元素为众数
 * 4. 最后剩下的假设众数就是真实众数
 *
 * 关键点：
 * - 依赖多数元素出现次数>n/2的特性
 * - 不同元素会互相抵消，最终剩下的一定是多数元素
 *
 * 时间复杂度：O(n) 只需一次遍历
 * 空间复杂度：O(1) 只使用常数空间
 */
public class MajorityElement {
    public int majorityElement(int[] nums) {
        int votes = 0;
        int target = -1;
        for (int num : nums) {
            if (votes == 0){
                target = num;
            }

            if (target == num){
                votes++;
            }else{
                votes--;
            }
        }
        return target;
    }
}
