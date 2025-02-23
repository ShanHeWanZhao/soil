package com.github.soil.algorithm.leetcode.h100.middle;

import com.github.soil.algorithm.leetcode.editor.cn.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * <a href="https://leetcode.cn/problems/binary-tree-level-order-traversal/?envType=study-plan-v2&envId=top-100-liked">
 *     二叉树的层序遍历</a>
 *
 * 思路：
 * 1. 使用 BFS（广度优先搜索）进行层序遍历
 * 2. 使用队列 Queue 存储当前层的节点
 * 3. 每次遍历一层，将该层的节点值存入 `result`
 * 4. 将当前层的左右子节点加入队列，继续遍历下一层
 *
 * 时间复杂度：O(N)，每个节点访问一次
 * 空间复杂度：O(N)，最坏情况下队列存储所有节点
 */
public class LevelOrder {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result; // 空树返回空列表
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root); // 根节点入队

        while (!queue.isEmpty()) {
            int currentLevelSize = queue.size(); // 当前层的节点数
            List<Integer> ans = new ArrayList<>(currentLevelSize);

            // 遍历当前层的所有节点
            for (int i = 0; i < currentLevelSize; i++) {
                TreeNode node = queue.poll();
                ans.add(node.val);

                // 左子节点入队
                if (node.left != null) {
                    queue.offer(node.left);
                }
                // 右子节点入队
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            result.add(ans); // 存储当前层的结果
        }
        return result;
    }
}

