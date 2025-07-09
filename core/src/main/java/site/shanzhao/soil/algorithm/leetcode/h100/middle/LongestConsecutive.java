package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.Arrays;
import java.util.HashSet;

/**
 * <a href="https://leetcode.cn/problems/longest-consecutive-sequence/?envType=study-plan-v2&envId=top-100-liked">
 *     最长连续序列
 *     </a>
 *
 */
public class LongestConsecutive {

    public static void main(String[] args) {
        LongestConsecutive consecutive = new LongestConsecutive();
        consecutive.longestConsecutive(new int[]{100,4,200,1,3,2});
    }
    public int longestConsecutive(int[] nums) {
         return method1(nums);
//         return method2(nums);
    }

    // 推荐：利用hash解决
    private int method1(int[] nums){
        HashSet<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }
        int maxLength = 0;
        for (Integer num : set) {
            if (!set.contains(num - 1)){// 当前num已为连续数字中最小的那个数字
                int current = 1;
                int nextNum = num + 1;
                while(set.contains(nextNum)){
                    current++;
                    nextNum++;
                }
                maxLength = Math.max(current, maxLength);
            }
        }
        return maxLength;
    }
    // 先排序
    private int method2(int[] nums) {
        if (nums == null || nums.length == 0){
            return 0;
        }
        int[] sortedNums = Arrays.stream(nums).distinct().sorted().toArray();
        System.out.println(Arrays.toString(sortedNums));
        int length = 1;
        int maxLength = 1;
        for (int i = 0; i < sortedNums.length; i++) {
            if (i > 0){
                if (sortedNums[i] == sortedNums[i - 1] + 1){
                    length++;
                    maxLength = Math.max(maxLength, length);
                }else {
                    length = 1;
                }
            }
        }
        return maxLength;
    }
}
