package site.shanzhao.soil.algorithm.leetcode.h100.simple;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.TreeNode;

/**
 * <a href="https://leetcode.cn/problems/convert-sorted-array-to-binary-search-tree/?envType=study-plan-v2&envId=top-100-liked">
 *     将有序数组转换为二叉搜索树</a>
 *     由于数组已升序排序，则中间元素就应该是当前子树的根节点。
 *     可以利用这个思想找到左子节点（即为根节点左边数组的中点）和右子节点（即根节点右边数组的中点）
 *
 * 思路：
 * 1. 使用递归 + 二分法
 * 2. 每次选择中间元素作为根节点，保证二叉搜索树平衡
 * 3. 左子树从 `left` 到 `middle-1` 递归构造
 * 4. 右子树从 `middle+1` 到 `right` 递归构造
 *
 * 时间复杂度：O(N)，每个元素都被访问一次
 * 空间复杂度：O(logN)，递归调用栈的深度
 */
public class SortedArrayToBST {
    public TreeNode sortedArrayToBST(int[] nums) {
        return dfs(nums, 0, nums.length - 1);
    }

    private TreeNode dfs(int[] nums, int left, int right) {
        if (left > right) { // 递归终止条件，子数组为空
            return null;
        }

        int middle = (left + right) / 2; // 选择中间节点作为根节点
        TreeNode root = new TreeNode(nums[middle]);

        // 递归构造左右子树
        root.left = dfs(nums, left, middle - 1);
        root.right = dfs(nums, middle + 1, right);

        return root;
    }
}
