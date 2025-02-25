package com.github.soil.algorithm.leetcode.h100.middle;

import com.github.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 * <a href="https://leetcode.cn/problems/validate-binary-search-tree/?envType=study-plan-v2&envId=top-100-liked">
 *     验证二叉搜索树</a>
 *     搜索二叉树的中序遍历就是排序后的结果，所以中序遍历是最简单的解法，我们可以很方便的保存前一个值，并在后续的校验上用到
 */
public class ValidBST {

    // ======================= 中序遍历 =======================
    // 记录前一个访问的值
    private Integer preVal = null;

    public boolean inorderValidBST(TreeNode root) {
        if (root == null){
            return true;
        }
        // 递归检查左子树
        boolean valid = inorderValidBST(root.left);
        // 校验：当前节点一定是大于前一个节点的值
        if (!valid || (preVal != null && root.val <= preVal)){
            return false;
        }
        // 更新前一个节点值
        preVal = root.val;
        // 递归检查右子树
        return inorderValidBST(root.right);
    }

    // ======================= 前序遍历 =======================
    public boolean isValidBST(TreeNode root) {
        if (root == null){
            return true;
        }
        return preorderValid(root, null, null);
    }

    private boolean preorderValid(TreeNode root, Integer lower, Integer upper){
        if (root == null){
            return true;
        }
        // 当前节点值必须在 (lower, upper) 范围内
        if (lower != null && root.val <= lower){
            return false;
        }
        if (upper != null && root.val >= upper){
            return false;
        }
        // 递归检查左右子树
        return preorderValid(root.left, lower, root.val) && preorderValid(root.right, root.val, upper);
    }
}
