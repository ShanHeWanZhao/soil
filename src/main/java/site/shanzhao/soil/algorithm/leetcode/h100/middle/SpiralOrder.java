package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.List;
/**
 * <a href="https://leetcode.cn/problems/spiral-matrix/?envType=study-plan-v2&envId=top-100-liked">
 *     螺旋矩阵</a> <><br/>
 *
 * 解题思路：
 * - 题目要求按照 顺时针螺旋顺序 遍历矩阵，并输出所有元素。
 * - 设定四个边界（上 `up`、下 `down`、左 `left`、右 `right`），每次遍历完一条边后，向内收缩该边，直到所有元素遍历完成。
 * - 遍历顺序：向右 → 向下 → 向左 → 向上，然后循环直到所有元素遍历完。
 *
 * 边界控制：
 * - 每次遍历 一整条边 后，收缩边界（例如 `up++` 代表上边界向下移动）。
 * - 边界交叉时（`up > down` 或 `left > right`），说明所有元素已经遍历完成，跳出循环。
 *
 * 时间复杂度： O(m * n)，每个元素恰好访问一次。
 * 空间复杂度： O(1)，除了存储结果的 `List` 以外，没有额外的空间使用。
 */
public class SpiralOrder {

    public List<Integer> spiralOrder(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        // 定义4个边界
        int up = 0; // 上边界
        int down = matrix.length - 1; // 下边界
        int left = 0; // 左边界
        int right = matrix[0].length - 1; // 右边界
        while(true){
            // 向右移动（固定上边界）
            if (outOfBounds(up, down, left, right)){
                break;
            }
            for (int i = up, j = left; j <= right; j++){
                result.add(matrix[i][j]);
            }
            up++;  // 右移完毕后，上边界向下移动
            // 向下
            if (outOfBounds(up, down, left, right)){
                break;
            }
            for (int i = up, j = right; i <= down; i++){
                result.add(matrix[i][j]);
            }
            right--;
            // 向左
            if (outOfBounds(up, down, left, right)){
                break;
            }
            for (int i = down, j = right; j >= left; j--){
                result.add(matrix[i][j]);
            }
            down--;
            // 向上
            if (outOfBounds(up, down, left, right)){
                break;
            }
            for (int i = down, j = left; i >= up; i--){
                result.add(matrix[i][j]);
            }
            left++;
        }
        return result;
    }

    /**
     * 每次都需要边界检查以跳出循环判断
     */
    private static boolean outOfBounds(int up, int down, int left, int right){
        return up > down || left > right;
    }
}
