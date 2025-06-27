package site.shanzhao.soil.algorithm.leetcode.h100.simple;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 * <a href="https://leetcode.cn/problems/invert-binary-tree/description/?envType=study-plan-v2&envId=top-100-liked">
 *     翻转二叉树</a>
 *     用到后序遍历（后续遍历适合先处理完左右子树，再回到跟节点来处理数据的情况）
 */
public class InvertTree {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return root;
        }
        // 递归翻转左右子树
        TreeNode leftNode = invertTree(root.left);
        TreeNode rightNode = invertTree(root.right);
        // 交换左右子树
        root.left = rightNode;
        root.right = leftNode;
        return root; // 返回翻转后的根节点
    }
}
