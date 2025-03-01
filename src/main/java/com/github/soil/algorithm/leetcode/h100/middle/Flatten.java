package com.github.soil.algorithm.leetcode.h100.middle;

import com.github.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 * <a href="https://leetcode.cn/problems/flatten-binary-tree-to-linked-list/?envType=study-plan-v2&envId=top-100-liked">
 *     二叉树展开为链表</a>
 *     两种方法：反向后序遍历 or 迭代法
 *    反向后序遍历 时空间复杂度都为O(N)。迭代时间复杂度为O(N)，但空间复杂度为O（1），更推荐
 *
 *    反向后序遍历核心：正常二叉树都是先处理左子树，再处理右子树。
 *    但我们这里反过来即变成了先序遍历的结果反向打印，这里先遍历出的结果就是链表的末尾，可以直接调整指针，不影响后续需要处理的节点。
 *
 *    迭代法和Morris遍历很类似，但因为是要求先序，
 *      所以当前节点的右子节点在先序遍历中的pre即是当前节点左子树中最右的节点（即左子树最右节点的next是当前节点的右子节点），
 *     而当前节点的左子节点在先序遍历中的pre即使当前节点（即当前节点的next是左子节点）
 *     所以根据这两个特性不断的移动节点位置，直到当前节点为空即处理完毕
 *
 *     方法总结：
 *  🔹 反向后序遍历 (Right -> Left -> Root)
 *     - 传统遍历顺序是左 -> 右 -> 根，我们反过来变成右 -> 左 -> 根。
 *     - 这样先遍历到的节点是最终链表的末尾，可以直接调整指针，不影响后续节点。
 *     - **时间复杂度 O(N)，空间复杂度 O(N)（递归栈空间）**。
 *  🔹 迭代法（基于 Morris 遍历）
 *     - 由于展开后的链表顺序是 **先序遍历 (Root -> Left -> Right)**，关键是正确调整左右子树：
 *       1. **左子树最右节点的 right 指向当前节点的 right**（确保右子树拼接到左子树的最右端）。
 *       2. **当前节点的 right 指向左子树**，然后置 `left = null`。
 *       3. **向右移动**，继续展开下一个节点。
 */
public class Flatten {

    // ========================== 方法1:反向后序遍历 ==========================
    // 当前已展开链表的头节点
    private TreeNode next;

    public void flatten(TreeNode root) {
        if (root == null){
            return;
        }
        // 递归处理右子树（先展开右子树）
        flatten(root.right);
        // 递归处理左子树（再展开左子树）
        flatten(root.left);

        // 让当前节点的右子树指向 next，并清空左子树
        root.right = next;
        root.left = null;
        // 更新 next 为当前节点
        next = root;
    }

    // ========================== 方法2:迭代（Morris 遍历） ==========================
    public void flattenByRecursion(TreeNode root) {
        if (root == null){
            return;
        }
        TreeNode cur= root;
        while (cur != null){
            // 若存在左子树
            if (cur.left != null) {
                TreeNode mostRight = cur.left;
                // 找到左子树的最右节点
                while (mostRight.right != null) {
                    mostRight = mostRight.right;
                }
                // 将右子树接到最右节点的右子树上
                mostRight.right = cur.right;
                // 左子树变为右子树，并清空左子树
                cur.right = cur.left;
                cur.left = null;
            }
            // 继续处理下一个节点
            cur = cur.right;

        }
    }

}

