package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/n-queens/?envType=study-plan-v2&envId=top-100-liked">
 *     51. N皇后</a>
 *
 *
 * 算法思路：
 * 1. 使用回溯法逐行放置皇后，尝试在每一行的每一列放置皇后。
 * 2. 在放置皇后时，检查当前列和对角线是否已经被其他皇后占据。
 * 3. 如果当前位置可以放置皇后，则递归处理下一行。
 * 4. 如果所有行都成功放置了皇后，则将当前棋盘状态加入结果集。
 * 5. 回溯时移除当前皇后，尝试其他可能的放置位置。
 *
 * 时间复杂度：O(N!)，其中N是棋盘的大小。
 *   - 每行有N种放置方式，但由于列和对角线的限制，实际搜索空间为N!。
 *
 * 空间复杂度：O(N^2)，用于存储棋盘的二维数组和递归调用栈。
 */
public class SolveNQueens {

    /**
     * 主方法，用于返回所有可能的N皇后放置方案。
     *
     * @param n 棋盘的大小
     * @return 所有可能的放置方案
     */
    public List<List<String>> solveNQueens(int n) {
        char[][] board = new char[n][n]; // 初始化棋盘
        for (char[] b : board) {
            Arrays.fill(b, '.'); // 将棋盘初始化为全'.'
        }
        int[] placed = new int[n]; // 记录每行皇后放置的列索引
        Arrays.fill(placed, -1); // 初始化为-1，表示未放置
        List<List<String>> result = new ArrayList<>();
        backtrack(n, 0, result, board, placed); // 回溯法生成所有放置方案
        return result;
    }

    /**
     * 回溯方法，用于生成所有可能的N皇后放置方案。
     *
     * @param n 棋盘的大小
     * @param row 当前处理的行索引
     * @param result 结果集，存储所有放置方案
     * @param board 当前棋盘状态
     * @param placed 记录每行皇后放置的列索引
     */
    private void backtrack(int n, int row, List<List<String>> result, char[][] board, int[] placed) {
        if (row == n) {
            // 如果所有行都成功放置了皇后，则将当前棋盘状态加入结果集
            List<String> path = new ArrayList<>();
            for (char[] b : board) {
                path.add(new String(b)); // 将每一行的字符数组转换为字符串
            }
            result.add(path);
            return;
        }
        for (int col = 0; col < n; col++) {
            if (canPlace(row, col, placed)) {
                // 如果当前位置可以放置皇后，则放置皇后并递归处理下一行
                placed[row] = col; // 记录当前行皇后放置的列索引
                board[row][col] = 'Q'; // 在棋盘上放置皇后
                backtrack(n, row + 1, result, board, placed); // 递归处理下一行
                placed[row] = -1; // 回溯，移除当前皇后
                board[row][col] = '.'; // 恢复棋盘状态
            }
        }
    }

    /**
     * 判断当前位置是否可以放置皇后。
     *
     * @param row 当前处理的行索引
     * @param col 当前处理的列索引
     * @param placed 记录每行皇后放置的列索引
     * @return 如果当前位置可以放置皇后则返回true，否则返回false
     */
    private boolean canPlace(int row, int col, int[] placed) {
        for (int i = 0; i < placed.length; i++) {
            if (placed[i] == -1) {
                continue; // 如果当前行未放置皇后，则跳过
            }
            // 检查是否在同一列或同一对角线上
            if (col == placed[i] || (Math.abs(row - i) == Math.abs(col - placed[i]))) {
                return false;
            }
        }
        return true;
    }
}