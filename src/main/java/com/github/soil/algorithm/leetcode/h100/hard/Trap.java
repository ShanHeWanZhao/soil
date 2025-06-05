package com.github.soil.algorithm.leetcode.h100.hard;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <a href="https://leetcode.cn/problems/trapping-rain-water/description/?envType=study-plan-v2&envId=top-100-liked">
 *    42.接雨水</a>
 *
 * 提供四种解法，分为两类计算维度：
 *
 * 【竖直方向计算】（前三种方法）：
 * 核心思想：对于每个柱子，计算其顶部能承接的雨水高度，即 min(左边最大高度, 右边最大高度) - 当前高度
 *
 * 1. 暴力法 - 每个柱子单独计算左右最大值，时间复杂度O(n^2)，空间复杂度O(1)
 * 2. 动态规划 - 预先存储每个柱子的左右最大值，时间复杂度O(n)，空间复杂度O(n)
 * 3. 双指针 - 边遍历边记录左右最大值，时间复杂度O(n)，空间复杂度O(1)
 *
 * 【横向计算】（第四种方法）：
 * 4. 单调栈 - 通过维护递减栈寻找"凹槽"区域，计算横向水层面积，时间复杂度O(n)，空间复杂度O(n)
 *    核心思想：当出现升高柱子时，与栈中前一个柱子形成凹槽，计算该层水的矩形面积（宽度×高度）
 */
public class Trap {

    /**
     * 解法一：暴力解法
     * 对每个柱子，向左、向右扫描，分别找最高的柱子，
     * 当前柱子能存水的量 = min(左边最高，右边最高) - 当前高度
     *
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     */
    public int trap1(int[] height) {
        int area = 0;
        // 遍历每个柱子（跳过第一个和最后一个，因为它们不能接水）
        for (int i = 1; i < height.length - 1; i++) {
            // 向左寻找最高柱子
            int leftMaxHeight = 0;
            for (int l = 0; l < i; l++) {
                leftMaxHeight = Math.max(height[l], leftMaxHeight);
            }
            // 向右寻找最高柱子
            int rightMaxHeight = 0;
            for (int r = i + 1; r < height.length; r++) {
                rightMaxHeight = Math.max(height[r], rightMaxHeight);
            }
            // 当前柱子能接的雨水量 = min(左最高,右最高) - 当前高度
            area = area + Math.max(0, Math.min(leftMaxHeight, rightMaxHeight) - height[i]);
        }
        return area;
    }

    /**
     * 解法二：动态规划（预处理左右两侧最大高度）
     * 预处理出每个位置左侧最大高度和右侧最大高度，避免重复扫描。
     *
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     */
    public int trap2(int[] height) {
        // 存储每个位置左边的最大值
        int[] leftMaxHeights = new int[height.length];
        for (int l = 1; l < height.length; l++) {
            leftMaxHeights[l] = Math.max(height[l - 1], leftMaxHeights[l - 1]);
        }

        // 存储每个位置右边的最大值
        int[] rightMaxHeights = new int[height.length];
        for (int r = height.length - 2; r >= 0; r--) {
            rightMaxHeights[r] = Math.max(height[r + 1], rightMaxHeights[r + 1]);
        }

        // 计算每个柱子的积水量
        int area = 0;
        for (int i = 1; i < height.length - 1; i++) {
            area = area + Math.max(0, Math.min(leftMaxHeights[i], rightMaxHeights[i]) - height[i]);
        }
        return area;
    }

    /**
     * 解法三：双指针法
     * 同时从左右向内移动指针，维护当前位置左/右最大高度。
     *
     * 核心思路：柱子能接多少水取决于当前柱子左右端最高柱子中较矮的那个
     *
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     */
    public int trap3(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int leftMaxHeight = 0;  // 记录左边最大值
        int rightMaxHeight = 0; // 记录右边最大值
        int area = 0;

        while (left < right) {
            // 哪边柱子矮就先处理哪边，这样能保证未移动的那根柱子高度大于所有历史移动过的最高柱子
            if (height[left] < height[right]) {
                // 更新左边最大值
                leftMaxHeight = Math.max(leftMaxHeight, height[left]);
                area += leftMaxHeight - height[left];
                left++;
            } else {
                // 更新右边最大值
                rightMaxHeight = Math.max(rightMaxHeight, height[right]);
                area += rightMaxHeight - height[right];
                right--;
            }
        }
        return area;

    }

    /**
     * 解法四：单调栈
     * 维护一个单调递减栈，当前柱子比栈顶高时，说明可以形成水坑。
     *
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     */
    public int trap4(int[] height) {
        int area = 0;
        Deque<Integer> stack = new ArrayDeque<>(); // 存储柱子索引的单调栈（递减）

        for (int i = 0; i < height.length; i++) {
            // 当 当前柱子高度大于栈顶柱子高度时，说明可能形成凹槽
            while (!stack.isEmpty() && height[stack.peek()] < height[i]) {
                int curHeightIndex = stack.pop(); // 凹槽底部
                if (!stack.isEmpty()) {
                    int width = i - stack.peek() - 1; // 计算宽度
                    int h = Math.min(height[i], height[stack.peek()]) - height[curHeightIndex]; // 计算高度
                    area += width * h; // 计算面积
                }
            }
            // 重点：就算是重复值，也要入栈
            stack.push(i);
        }
        return area;
    }
}
