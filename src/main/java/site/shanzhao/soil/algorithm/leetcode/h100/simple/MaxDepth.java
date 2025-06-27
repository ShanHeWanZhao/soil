package site.shanzhao.soil.algorithm.leetcode.h100.simple;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 * <a href="https://leetcode.cn/problems/maximum-depth-of-binary-tree/description/?envType=study-plan-v2&envId=top-100-liked">
 *     二叉树的最大深度</a>
 * 递归 DFS 求二叉树最大深度
 * - 时间复杂度：O(n)，每个节点访问一次
 * - 空间复杂度：O(n)（最坏情况，退化为链表）/ O(log n)（平衡二叉树）
 */
public class MaxDepth {
    public int maxDepth(TreeNode root) {
        if (root == null){
            return 0;
        }
        // 左子树深度
        int leftDepth = maxDepth(root.left);
        // 右子树深度
        int rightDepth = maxDepth(root.right);
        // 当前层最大深度 = 左右子树的最大深度 + 1
        return Math.max(leftDepth, rightDepth) + 1;
    }
}
