package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.TreeNode;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://leetcode.cn/problems/path-sum-iii/?envType=study-plan-v2&envId=top-100-liked">
 *     路径总和</a>
 *     类比数组中求连续子区间的和
 *
 * 整体思路：
 * 1. 使用前缀和技巧，将树上路径和转化为类似数组前缀和的处理方式
 * 2. 维护从根到当前节点路径上的前缀和及其出现次数
 * 3. 通过当前前缀和与目标值的差值，查找是否存在符合条件的历史前缀和
 * 4. 使用回溯思想，在遍历完子树后恢复状态
 *
 * 关键要点：
 * - 使用前缀和消除重复计算：curSum - targetSum = historySum
 * - 使用HashMap存储前缀和的出现次数
 * - 使用long类型避免整数溢出
 * - 初始化map.put(0L, 1)处理从根节点开始的路径
 *
 * 时间复杂度：O(n)，其中n为节点数量
 * - 每个节点只遍历一次
 * - HashMap操作的时间复杂度为O(1)
 *
 * 空间复杂度：O(h)，其中h为树的高度
 * - 递归调用栈的空间为O(h)
 * - HashMap存储的前缀和数量不会超过树的高度
 */
public class PathSum {

    // 记录符合条件的路径数量
    private int count = 0;
    // 当前路径的前缀和
    private long preSum = 0;

    public int pathSum(TreeNode root, int targetSum) {
        if (root == null) {
            return 0;
        }
        // 初始化前缀和Map，放入0便于处理从根节点开始的路径
        Map<Long, Integer> map = new HashMap<>();
        map.put(0L, 1);
        dfs(root, targetSum, map);
        return count;
    }

    /**
     * 深度优先搜索处理每个节点
     *
     * @param root 当前节点
     * @param targetSum 目标和
     * @param map 存储前缀和及其出现次数的映射
     */
    private void dfs(TreeNode root, int targetSum, Map<Long, Integer> map) {
        if (root == null) {
            return;
        }

        // 计算当前路径的前缀和
        long curSum = root.val + preSum;
        preSum = curSum;

        // 查找是否存在历史前缀和，使得当前前缀和减去该历史前缀和等于目标值
        count = count + map.getOrDefault(curSum - targetSum, 0);

        // 将当前前缀和加入映射
        map.put(curSum, map.getOrDefault(curSum, 0) + 1);

        // 递归处理左右子树
        dfs(root.left, targetSum, map);
        dfs(root.right, targetSum, map);

        // 回溯：恢复状态
        map.put(curSum, map.get(curSum) - 1);  // 移除当前前缀和的一次出现
        preSum = preSum - root.val;  // 恢复前缀和的值
    }
}
