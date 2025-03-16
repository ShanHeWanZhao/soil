package com.github.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/search-a-2d-matrix/description/?envType=study-plan-v2&envId=top-100-liked">
 *     74. 搜索二维矩阵</a>
 *     将其看作一维，计算出来middle的长度，再将middle转换成二维坐标进行收缩
 */
public class SearchMatrixByBinary {

    public boolean searchMatrix(int[][] matrix, int target) {
        int left = 0;
        int right = matrix.length * matrix[0].length - 1;
        while (right >= left) {
            int middle = (right + left) / 2;
            int row = middle / matrix[0].length;
            int col = middle % matrix[0].length;
            if (matrix[row][col] == target){
                return true;
            }else if (matrix[row][col] < target){
                left = middle + 1;
            }else{
                right = right - 1;
            }
        }
        return false;
    }
}
