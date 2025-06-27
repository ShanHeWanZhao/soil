package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.TreeNode;
/**
 * @see <a href="https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-tree/?envType=study-plan-v2&envId=top-100-liked">
 *      二叉树的最近公共祖先</a>
 *
 * 二叉树的最近公共祖先(LCA)解决方案
 *
 * 整体思路：
 * 1. 使用后序遍历（左右根）的递归方式自底向上查找
 * 2. 递归函数返回值的含义：
 *    - 如果找到p或q，返回找到的节点
 *    - 如果未找到p和q，返回null
 *    - 如果找到最近公共祖先，返回最近公共祖先节点
 * 3. 通过左右子树的返回值判断当前节点是否为最近公共祖先
 *
 * 关键要点：
 * - 后序遍历保证自底向上查找，首先处理子树情况
 * - 三种情况的判断：
 *   1. 左右子树都不为空：说明p、q分别在左右子树，当前节点为LCA
 *   2. 左右子树都为空：说明p、q都不在当前子树中
 *   3. 一个子树不为空：返回不为空的子树的结果
 *
 * 时间复杂度：O(n)，其中n为节点数量
 * - 最坏情况下需要遍历整棵树
 *
 * 空间复杂度：O(h)，其中h为树的高度
 * - 递归调用栈的最大深度为树的高度
 * - 最坏情况下，对于斜树，空间复杂度为O(n)
 *
 */
public class LowestCommonAncestor {

    /**
     * 查找二叉树中两个节点的最近公共祖先
     *
     * @param root 二叉树根节点
     * @param p 第一个目标节点
     * @param q 第二个目标节点
     * @return 最近公共祖先节点
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // 基本情况：空节点表示没找到，直接返回null
        if (root == null) {
            return null;
        }
        // 如果当前节点是p或q，直接返回当前节点
        if (root == p || root == q) {
            return root;
        }

        // 递归搜索左右子树
        TreeNode leftNode = lowestCommonAncestor(root.left, p, q);
        TreeNode rightNode = lowestCommonAncestor(root.right, p, q);

        // 情况1：左右子树都找到了节点，说明当前节点就是LCA
        if (leftNode != null && rightNode != null) {
            return root;
        }

        // 情况2：左右子树都没找到，返回null
        if (leftNode == null && rightNode == null) {
            return null;
        }

        // 情况3：只有一个子树找到了节点，返回找到的那个节点
        return leftNode != null ? leftNode : rightNode;
    }
}
