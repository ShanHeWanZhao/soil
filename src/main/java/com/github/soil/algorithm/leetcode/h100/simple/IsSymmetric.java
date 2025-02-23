package com.github.soil.algorithm.leetcode.h100.simple;

import com.github.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 * <a href="https://leetcode.cn/problems/symmetric-tree/?envType=study-plan-v2&envId=top-100-liked">
 *    对称二叉树（即镜像二叉树）</a>
 */
public class IsSymmetric {
    public boolean isSymmetric(TreeNode root) {
        if (root == null){
            return true;
        }
        return isSymmetric(root.left, root.right);
    }

    /**
     * 递归检查两个子树是否镜像对称
     */
    private boolean isSymmetric(TreeNode left, TreeNode right){
        // 1. 两个子树都为空，返回 true（对称）
        if (left == null && right == null) {
            return true;
        }
        // 2. 只有一个为空，返回 false（不对称）
        if (left == null || right == null) {
            return false;
        }
        // 3. 两个节点值不同，返回 false（不对称）
        if (left.val != right.val) {
            return false;
        }
        // 4. 递归检查：
        //    - left.left 和 right.right 是否对称
        //    - left.right 和 right.left 是否对称
        return isSymmetric(left.left, right.right) && isSymmetric(left.right, right.left);
    }
}
