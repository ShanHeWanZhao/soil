package com.github.soil.algorithm.leetcode.h100.middle;

import com.github.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 * <a href="https://leetcode.cn/problems/kth-smallest-element-in-a-bst/?envType=study-plan-v2&envId=top-100-liked">
 *     二叉搜索树中第 K 小的元素</a>
 *     还是运用了中序遍历的特性（对搜索二叉树排序打印），第k次遍历到的数就是我们要找的目标值
 */
public class KthSmallest {

    // 记录遍历到的节点个数
    private int count = 0;
    // 存储最终找到的第 K 小元素
    private Integer result;

    public int kthSmallest(TreeNode root, int k) {
        dfs(root, k);
        return result;
    }

    /**
     *  递归查找第 K 小的元素，找到后立即返回，不再继续遍历后续节点
     * @param root 当前节点
     * @param k 目标排名
     * @return 是否找到目标元素
     */
    private boolean dfs(TreeNode root, int k){
        if (root == null){
            return false;
        }
        // 递归左子树，优先查找更小的元素
        if (dfs(root.left, k)) {
            return true; // 找到后提前结束
        }

        // 访问当前节点
        if (++count == k) {
            result = root.val; // 记录第 K 小的元素
            return true; // 立即返回，不再继续遍历
        }

        // 递归右子树，寻找更大的元素
        return dfs(root.right, k);
    }
}
