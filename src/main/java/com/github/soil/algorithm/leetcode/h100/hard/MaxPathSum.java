package com.github.soil.algorithm.leetcode.h100.hard;

import com.github.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 *
 * @see <a href="https://leetcode.cn/problems/binary-tree-maximum-path-sum/?envType=study-plan-v2&envId=top-100-liked">
 *      二叉树中的最大路径和</a>
 *
 *
 * 整体思路：
 * 1. 使用后序遍历（左右根）计算每个节点的最大贡献值
 * 2. 维护全局最大路径和
 * 3. 每个节点有两个角色：
 *    - 作为路径中的普通节点（非最高节点）：只能选择一个子树加上自己，供父节点使用
 *    - 作为路径的最高节点：可以连接左右子树，更新全局最大值
 *
 * 关键要点：
 * - 贡献值的概念：从某个节点到叶子节点的最大路径和
 * - 负值的处理：如果子树贡献为负，直接舍弃（取0）
 * - 两种计算方式：
 *   1. 更新全局最大值：可以使用当前节点连接左右子树
 *   2. 返回贡献值：只能选择一条路径供父节点使用
 *
 * 时间复杂度：O(n)，其中n为节点数量
 * - 每个节点只遍历一次
 *
 * 空间复杂度：O(h)，其中h为树的高度
 * - 递归调用栈的空间，最坏情况下（斜树）为O(n)
 *
 */
public class MaxPathSum {
    // 保存全局最大路径和
    private Integer maxSum = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        dfs(root);
        return maxSum;
    }

    /**
     * 深度优先搜索计算每个节点的最大贡献值
     *
     * @param root 当前节点
     * @return 以当前节点为起点的最大贡献值（用于父节点计算）
     */
    private int dfs(TreeNode root) {
        // 基本情况：空节点贡献值为0
        if (root == null) {
            return 0;
        }

        // 计算左子树的最大贡献值，负值舍弃取0
        int leftMax = Math.max(0, dfs(root.left));
        // 计算右子树的最大贡献值，负值舍弃取0
        int rightMax = Math.max(0, dfs(root.right));

        // 更新全局最大路径和
        // 当前节点作为最高点，可以连接左右子树
        maxSum = Math.max(maxSum, leftMax + rightMax + root.val);

        // 返回节点的最大贡献值，供父节点计算使用
        // 只能选择左右子树中的一条路径
        return Math.max(leftMax, rightMax) + root.val;
    }
}
