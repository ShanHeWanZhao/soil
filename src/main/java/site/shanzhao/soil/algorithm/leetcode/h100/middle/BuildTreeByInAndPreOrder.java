package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.TreeNode;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://leetcode.cn/problems/construct-binary-tree-from-preorder-and-inorder-traversal/?envType=study-plan-v2&envId=top-100-liked">
 *     从前序与中序遍历序列构造二叉树</a>

 * 整体思路：
 * 1. 利用前序遍历的特点定位根节点（第一个元素必定是根节点）
 * 2. 通过中序遍历确定左右子树的范围（根节点左边是左子树，右边是右子树）
 * 3. 使用分治思想递归构建整棵树
 *
 * 关键要点：
 * - 前序遍历特点：[根节点][左子树][右子树]
 * - 中序遍历特点：[左子树][根节点][右子树]
 * - 使用HashMap存储中序遍历的索引位置，优化查找效率
 *
 * 时间复杂度：O(n)，其中n为节点数量
 * - 构建过程中每个节点只访问一次
 * - HashMap查找时间为O(1)
 *
 * 空间复杂度：O(n)
 * - HashMap存储所有节点的索引，需要O(n)空间
 * - 递归调用栈深度为树的高度，最坏情况（斜树）为O(n)
 *
 */
public class BuildTreeByInAndPreOrder {

    // 存储中序遍历中值到索引的映射，用于O(1)时间获取根节点位置
    private final Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        // 构建中序遍历的值到索引的映射
        for (int i = 0; i < inorder.length; i++) {
            indexMap.put(inorder[i], i);
        }
        return buildByDfs(preorder, 0, preorder.length - 1, 0);
    }

    /**
     * 使用深度优先搜索递归构建二叉树
     *
     * @param preorder       前序遍历数组
     * @param preLeft       当前子树在前序遍历中的起始位置
     * @param preRight      当前子树在前序遍历中的结束位置
     * @param inLeft        当前子树在中序遍历中的起始位置
     * @return 返回构建的子树根节点
     */
    private TreeNode buildByDfs(int[] preorder, int preLeft, int preRight, int inLeft) {
        // 基本情况：范围无效时返回null
        if (preLeft > preRight) {
            return null;
        }

        // 前序遍历中的第一个元素必定是根节点
        int rootVal = preorder[preLeft];
        TreeNode root = new TreeNode(rootVal);

        // 在中序遍历中找到根节点的位置，用于确定左右子树的范围
        Integer inorderRootIndex = indexMap.get(rootVal);

        // 递归构建左子树：
        // - 前序范围：从根节点后一位开始，长度为中序遍历中左子树的长度
        // - 中序范围：从当前中序起始位置开始
        root.left = buildByDfs(preorder, preLeft + 1,
                inorderRootIndex - inLeft + preLeft, inLeft);

        // 递归构建右子树：
        // - 前序范围：接着左子树之后，直到当前子树的末尾
        // - 中序范围：从根节点后一位开始
        root.right = buildByDfs(preorder, inorderRootIndex - inLeft + preLeft + 1,
                preRight, inorderRootIndex + 1);

        return root;
    }
}
