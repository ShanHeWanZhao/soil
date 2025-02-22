package com.github.soil.algorithm.leetcode.h100.simple;


import com.github.soil.algorithm.leetcode.editor.cn.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/binary-tree-inorder-traversal/description/?envType=study-plan-v2&envId=top-100-liked">
 *     二叉树的中序遍历
 * </a>
 *
 * 中序遍历（Inorder Traversal）
 * 1. 遍历顺序：左子树 → 当前节点 → 右子树
 * 2. 递归的 DFS 过程：
 *    - 第一次访问：刚到该节点（先递归左子树）
 *    - 第二次访问：左子树遍历完毕，回溯到当前节点（访问当前节点）
 *    - 第三次访问：右子树遍历完毕，回溯到当前节点（再递归右子树）
 * 3. 由此可知：
 *    - 前序遍历（当前节点 → 左子树 → 右子树）：在第一次访问时处理当前节点
 *    - 中序遍历（左子树 → 当前节点 → 右子树）：在第二次访问时处理当前节点
 *    - 后序遍历（左子树 → 右子树 → 当前节点）：在第三次访问时处理当前节点
 *
 * 时间复杂度：
 * - 递归调用每个节点恰好访问一次，时间复杂度为 O(n)。
 *
 * 空间复杂度：
 * - 最坏情况下（退化为链表），递归调用栈深度为 O(n)。
 * - 平衡二叉树时，递归调用栈深度 O(log n)。
 */
public class InorderTraversal {


    /**
     * **递归解法**
     * - 时间复杂度：O(n)，每个节点访问一次
     * - 空间复杂度：最坏 O(n)（退化为链表），最优 O(log n)（平衡二叉树）
     */
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null){
            return result;
        }
        dfs(root,result);
        return result;
    }
    private void dfs(TreeNode root, List<Integer> result){
        if (root == null){
            return;
        }
        dfs(root.left, result);  // 先递归遍历左子树
        result.add(root.val);     // 处理当前节点
        dfs(root.right, result);  // 再递归遍历右子树

    }

    /**
     * Morris 遍历（Morris Inorder Traversal）
     * - Morris 算法是一种 O(1) 空间复杂度 的遍历方法
     * - 通过 修改二叉树的结构 来避免使用递归或栈
     *
     * 核心思路：
     * 1. 如果 `cur` 没有左子树，则访问 `cur`，然后向右移动。
     * 2. 如果 `cur` 有左子树：
     *    - 找到 `cur` 左子树的 最右节点 mostRight：
     *      - 如果 `mostRight.right == null`，说明是第一次访问 `cur`：
     *        - 建立临时连接 `mostRight.right = cur`
     *        - `cur` 移动到 `cur.left`
     *      - 如果 `mostRight.right == cur`，说明是第二次访问 `cur`：
     *        - 恢复二叉树结构（`mostRight.right = null`）
     *        - 访问 `cur`
     *        - `cur` 移动到 `cur.right`
     *
     * 时间复杂度：
     * - O(n)，每个节点最多访问 2 次
     *
     * 空间复杂度：
     * - O(1)，只使用了有限的指针变量
     */

    public List<Integer> inorderTraversalByMirrors(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null){
            return result;
        }
        TreeNode cur = root;
        while (cur != null){
            if (cur.left == null){
                // 没有左子树，直接访问当前节点并移动到右子树
                result.add(cur.val);
                cur =  cur.right;
            }else{
                // 查找左子树中的最右节点（中序遍历的前驱节点）
                TreeNode mostRight = cur.left;
                while(mostRight.right != null && mostRight.right != cur){
                    mostRight = mostRight.right;
                }
                if (mostRight.right == null){
                    // 第一次遍历到 cur：建立临时连接，标记返回点
                    mostRight.right = cur;
                    cur = cur.left;
                }else{ // 第二次遍历到 cur：恢复二叉树结构，并访问当前节点
                    result.add(cur.val);
                    mostRight.right = null;
                    cur = cur.right;
                }
            }
        }
        return result;
    }
}
