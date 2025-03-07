package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.LinkedList;
import java.util.Queue;

/**
 * <a href="https://leetcode.cn/problems/rotting-oranges/description/?envType=study-plan-v2&envId=top-100-liked">
 *     腐烂的橘子</a>

 * 整体思路：
 * 1. 使用广度优先搜索(BFS)模拟橘子腐烂的传播过程
 * 2. 从所有初始腐烂的橘子同时开始扩散（多源BFS）
 * 3. 每一轮扩散代表一分钟，计算总共需要多少分钟使所有新鲜橘子腐烂
 * 4. 如果最后还有新鲜橘子无法被腐烂，返回-1
 *
 * 关键要点：
 * - 使用队列(Queue)实现BFS，队列中存储腐烂橘子的坐标
 * - 使用计数器记录新鲜橘子的数量，避免最后再次遍历网格
 * - 使用方向数组简化四个方向的遍历
 * - 按层次处理队列，每层代表一分钟的传播
 *
 * 时间复杂度：O(m*n)，其中m和n为网格的行数和列数
 * - 最坏情况下需要遍历整个网格两次
 *   1. 初始统计腐烂橘子和新鲜橘子
 *   2. BFS过程中每个格子至多被访问一次
 *
 * 空间复杂度：O(m*n)
 * - 最坏情况下，队列可能需要存储接近m*n个腐烂橘子的坐标
 */
public class OrangesRotting {

    /**
     * 计算使所有橘子腐烂所需的最小分钟数
     *
     * @param grid 二维网格，0表示空格，1表示新鲜橘子，2表示腐烂的橘子
     * @return 使所有橘子腐烂的分钟数，如果不可能则返回-1
     */
    public int orangesRotting(int[][] grid) {
        // 记录新鲜橘子的数量
        int fresh = 0;

        // 定义感染的四个方向：上、下、左、右
        int[][] directions = new int[][] { { -1, 0 }, { +1, 0 }, { 0, -1 }, { 0, +1 } };

        // 创建队列存储腐烂橘子的坐标
        Queue<int[]> queue = new LinkedList<>();

        // 第一次遍历网格：
        // 1. 找出所有初始腐烂的橘子，加入队列
        // 2. 统计新鲜橘子的数量
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 2) {
                    // 将腐烂橘子的坐标加入队列
                    int[] rotten = new int[] { i, j };
                    queue.offer(rotten);
                } else if (grid[i][j] == 1) {
                    // 统计新鲜橘子数量
                    fresh++;
                }
            }
        }

        // 记录经过的分钟数
        int minutes = 0;

        // 当队列不为空且还有新鲜橘子时进行BFS
        while (!queue.isEmpty() && fresh > 0) {
            // 获取当前层的大小（当前轮次要处理的腐烂橘子数量）
            int currentSize = queue.size();
            // 每一层代表一分钟
            minutes++;

            // 处理当前层的所有腐烂橘子
            for (int i = 0; i < currentSize; i++) {
                int[] rotten = queue.poll();

                // 检查四个方向的相邻橘子
                for (int[] direction: directions) {
                    int row = rotten[0] + direction[0];
                    int col = rotten[1] + direction[1];

                    // 检查边界和是否为新鲜橘子
                    if (row < 0 || row > grid.length - 1 ||
                            col < 0 || col > grid[0].length - 1 ||
                            grid[row][col] != 1) {
                        continue;
                    }

                    // 将新腐烂的橘子加入队列
                    queue.offer(new int[]{row, col});
                    // 将新鲜橘子标记为腐烂
                    grid[row][col] = 2;
                    // 减少新鲜橘子计数
                    fresh--;
                }
            }
        }

        // 如果还有新鲜橘子，说明它们无法被腐烂，返回-1
        // 否则返回经过的分钟数
        return fresh > 0 ? -1 : minutes;
    }

}
