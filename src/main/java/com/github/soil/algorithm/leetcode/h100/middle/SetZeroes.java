package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/set-matrix-zeroes/description/?envType=study-plan-v2&envId=top-100-liked">
 *
 *    矩阵置0</a>
 *    这道题的目标是要在：时间复杂度为O(mn)，空间复杂度为0(1)的范围内解决。（m为矩阵的行数，n为矩阵列数）<><br/>
 *
 *    核心思想：将二维数组想象成矩阵，矩阵的第1行（即matrix[0][i]）用来记录当前列是否置0，矩阵的第1列（即matrix[i][0]）用来记录当前行是否置0，
 *    由于第一行和第一列会被作为标记，所以我们要先计算出第一行和第一列是否该被置为0，后续在置0时先跳过第一行和第一列，最后再来处理它。
 *
 *    具体思路如下：
 * 1. **利用矩阵的第一行和第一列作为标记位**
 *    - 用 **matrix[0][j]** 标记第 `j` 列是否需要置 0。
 *    - 用 **matrix[i][0]** 标记第 `i` 行是否需要置 0。
 *    - 由于第一行和第一列被用作标记，我们需要 **额外记录第一行和第一列是否本身需要置 0**，否则会影响最终的结果。
 *
 * 2. **先遍历矩阵，记录需要置 0 的行和列**
 *    - 先遍历 **第一行** 和 **第一列**，记录它们是否本身需要变 0（因为它们会被覆盖）。
 *    - 再遍历剩余的矩阵（不包括第一行第一列），**如果 `matrix[i][j] == 0`，就将 `matrix[0][j]` 和 `matrix[i][0]` 置 0** 作为标记。
 *
 * 3. **根据标记置 0**
 *    - 从 **第二行和第二列** 开始，如果 `matrix[i][0] == 0` 或 `matrix[0][j] == 0`，则 `matrix[i][j] = 0`。
 *
 * 4. **最后单独处理第一行和第一列**
 *    - 如果第一行本身需要置 0，则全置 0。
 *    - 如果第一列本身需要置 0，则全置 0。
 */
public class SetZeroes {
    public void setZeroes(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return;
        }
        boolean firstRowHasZero = false;
        boolean firstColHasZero = false;
        // **1. 记录第一行是否有 0**
        for (int i = 0; i < matrix[0].length; i++) {
            if (matrix[0][i] == 0) {
                firstRowHasZero = true;
                break;
            }
        }
        // **2. 记录第一列是否有 0**
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][0] == 0) {
                firstColHasZero = true;
                break;
            }
        }
        // **3. 使用第一行和第一列作为标记，记录哪些行列需要置 0**
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    matrix[0][j] = 0;
                    matrix[i][0] = 0;
                }
            }
        }
        // **4. 处理标记过的行和列**

        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                if (matrix[0][j] == 0 || matrix[i][0] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }
        // **5. 最后处理第一行**
        if (firstRowHasZero) {
            for (int i = 0; i < matrix[0].length; i++) {
                matrix[0][i] = 0;
            }
        }
        // **6. 最后处理第一列**
        if (firstColHasZero) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i][0] = 0;
            }
        }

    }

}
