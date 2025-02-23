package com.github.soil.algorithm.leetcode.h100.simple;

import com.github.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 * <a href="https://leetcode.cn/problems/diameter-of-binary-tree/?envType=study-plan-v2&envId=top-100-liked">
 *     二叉树的直径</a>
 *
 * 思路：
 * 1. 计算每个节点的左右子树最大深度
 * 2. 计算经过该节点的路径长度：`左子树最大深度 + 右子树最大深度 + 2`
 * 3. 在遍历过程中维护最大直径 `diameter`
 * 4. 递归返回当前节点的最大深度（供父节点使用）
 *
 * 时间复杂度：O(N) - 每个节点仅遍历一次
 * 空间复杂度：O(H) - 递归深度取决于树的高度（最坏 O(N)，最优 O(logN)）
 */
public class DiameterOfBinaryTree {
    private int diameter = 0; // 存储最大直径

    public int diameterOfBinaryTree(TreeNode root) {
        if (root == null) {
            return diameter;
        }
        currentNodeDiameter(root);
        return diameter;
    }

    /**
     * 计算当前节点的最大深度，并更新直径
     *
     * @param root 当前节点
     * @return 当前节点的最大深度（即左右子树的最大深度 + 1）
     */
    private int currentNodeDiameter(TreeNode root) {
        if (root == null) {
            return -1; // 叶子节点的子节点深度定义为 -1
        }

        // 计算左右子树的最大深度
        int leftDiameter = currentNodeDiameter(root.left);
        int rightDiameter = currentNodeDiameter(root.right);

        // 更新最大直径（当前节点的最大路径 = 左深度 + 右深度 + 2）
        diameter = Math.max(diameter, leftDiameter + rightDiameter + 2);

        // 返回当前节点的最大深度（+1 是因为要算上当前节点自身）
        return Math.max(leftDiameter, rightDiameter) + 1;
    }
}

