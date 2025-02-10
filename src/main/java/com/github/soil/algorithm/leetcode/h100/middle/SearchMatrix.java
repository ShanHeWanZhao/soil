package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/search-a-2d-matrix-ii/?envType=study-plan-v2&envId=top-100-liked">
 *     搜索二维矩阵</a>
 *     - 题目要求在 行和列均递增的矩阵 中查找 `target` 是否存在。
 *     核心思想：将矩阵右上角的点看成二叉树的顶点，这个点左边的数小于它，而下边的数大于它<><br/>
 *
 * 解题思路：
 * - 从右上角开始搜索，因为：
 *   - 左侧元素较小（比当前元素小）
 *   - 下方元素较大（比当前元素大）
 * - 这样，我们可以用类似 二叉搜索树的搜索方式 来查找：
 *   - 若当前元素 > target，说明 `target` 在当前列左侧，向左移动 `col--`
 *   - 若当前元素 < target，说明 `target` 在当前行下方，向下移动 `row++`
 *   - 若当前元素 == target，直接返回 `true`
 * - 终止条件：超出矩阵边界 `row >= matrix.length` 或 `col < 0` 时，返回 `false`
 *
 * 时间复杂度： O(m + n)（最多移动 `m` 行 + `n` 列）
 * 空间复杂度： O(1)（仅使用常数额外空间）
 */
public class SearchMatrix {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        // 从右上角开始搜索
        int row = 0;
        int col = matrix[0].length - 1;
        while(row < matrix.length && col >= 0){
            if (matrix[row][col] > target){ // 当前元素大于 `target`，向左移动（缩小搜索范围）
                col--;
            }else if (matrix[row][col] < target){ // 当前元素小于 `target`，向下移动（扩大搜索范围）
                row++;
            }else { // 找到直接返回
                return true;
            }
        }
        return false;
    }
}
