package com.github.soil.algorithm.leetcode.h100.middle;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/merge-intervals/">
 *     合并区间</a>
 *
 * 解题思路：
 * 1. 先按区间的起始位置对所有区间进行排序
 * 2. 遍历排序后的区间，比较当前区间与前一个区间的关系：
 *    - 如果当前区间的起点 > 前一个区间的终点：说明两区间不相交，可以将前一个区间加入结果
 *    - 否则说明两区间相交，更新前一个区间的终点为两区间终点的最大值
 * 3. 最后需要将最后一个区间加入结果
 */
public class IntervalMerge {
    public int[][] merge(int[][] intervals) {
        // 处理边界情况
        if (intervals == null || intervals.length == 1) {
            return intervals;
        }

        // 按区间起点排序
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0]));

        // 记录当前处理的区间的起点和终点
        int preIntervalLeft = intervals[0][0];
        int preIntervalRight = intervals[0][1];
        List<int[]> listResult = new ArrayList<>();

        // 遍历所有区间
        for (int[] interval : intervals) {
            if (interval[0] > preIntervalRight) {
                // 当前区间与前一个区间不相交，将前一个区间加入结果
                listResult.add(new int[]{preIntervalLeft, preIntervalRight});
                preIntervalLeft = interval[0];
                preIntervalRight = interval[1];
            } else {
                // 区间相交，合并区间
                preIntervalRight = Math.max(preIntervalRight, interval[1]);
            }
        }
        // 加入最后一个区间
        listResult.add(new int[]{preIntervalLeft, preIntervalRight});

        return listResult.toArray(new int[listResult.size()][]);
    }
}
