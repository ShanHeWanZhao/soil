package com.github.soil.algorithm.leetcode.h100.simple;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/pascals-triangle/?envType=study-plan-v2&envId=top-100-liked">
 *     118. 杨辉三角</a>
 *     状态转移方程：dp[i][j] = dp[i-1][j-1] + dp[i-1][j]
 *     dp[i][j]表示第i行第j列的数字为多少，首先保证每一行的第一个和最后一个数组为1
 */
public class Generate {

    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < numRows; i++){
            List<Integer> ans = new ArrayList<>();
            for (int j = 0;j <= i; j++){
                if (j == 0 || j == i){
                    ans.add(1);
                }else{
                    ans.add(result.get(i - 1).get(j - 1) + result.get(i - 1).get(j));
                }
            }
            result.add(ans);
        }
        return result;
    }
}
