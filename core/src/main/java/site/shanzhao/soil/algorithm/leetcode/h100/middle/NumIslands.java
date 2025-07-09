package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/number-of-islands/?envType=study-plan-v2&envId=top-100-liked">岛屿数量</a>

 *
 * 整体思路：
 * 1. 遍历整个二维网格，每当发现一个未访问的陆地（'1'），计数器加1
 * 2. 使用深度优先搜索(DFS)标记与当前陆地相连的所有陆地（同一个岛屿）
 * 3. 标记过程中将访问过的陆地修改为水域（'0'），避免重复计数
 * 4. 返回最终计数结果
 *
 * 关键要点：
 * - 采用"淹没"思想：将已经访问过的陆地变成水域，从而避免重复访问
 * - DFS递归搜索四个方向（上、下、左、右）连接的陆地
 * - 递归终止条件：越界或当前格子为水域（'0'）
 *
 * 时间复杂度：O(m*n)，其中m和n为网格的行数和列数
 * - 最坏情况下需要遍历整个网格，且每个格子至多被访问一次
 *
 * 空间复杂度：O(m*n)
 * - 最坏情况下，整个网格都是陆地，DFS的递归深度可能达到m*n
 */
public class NumIslands {
    /**
     * 计算二维网格中岛屿的数量
     *
     * @param grid 由'0'(水)和'1'(陆地)组成的二维网格
     * @return 岛屿的数量
     */
    public int numIslands(char[][] grid) {
        int result = 0;  // 记录岛屿数量

        // 遍历整个网格，查找陆地
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // 当找到一块未访问的陆地时
                if (grid[i][j] == '1') {
                    result++;  // 发现新岛屿，计数器加1
                    // 通过DFS标记与当前陆地相连的所有陆地
                    dfs(grid, i, j);
                }
            }
        }
        return result;
    }

    /**
     * 深度优先搜索标记与当前位置相连的所有陆地
     *
     * @param grid 二维网格
     * @param row 当前行位置
     * @param col 当前列位置
     */
    private void dfs(char[][] grid, int row, int col) {
        // 边界检查和水域检查：
        // 1. 行或列超出网格范围
        // 2. 当前位置已经是水域
        if (row < 0 || row > grid.length - 1 ||
                col < 0 || col > grid[0].length - 1 ||
                grid[row][col] == '0') {
            return;
        }

        // 将当前陆地标记为已访问（变为水域）
        grid[row][col] = '0';

        // 递归搜索四个方向的相邻格子
        dfs(grid, row - 1, col);  // 上
        dfs(grid, row + 1, col);  // 下
        dfs(grid, row, col - 1);  // 左
        dfs(grid, row, col + 1);  // 右
    }

}
